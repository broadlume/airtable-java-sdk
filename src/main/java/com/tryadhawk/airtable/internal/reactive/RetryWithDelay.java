/*
 * Copyright 2020, Airtable-java Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.tryadhawk.airtable.internal.reactive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Function;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for retrying a reactive operation until it succeeds while waiting a random amount of time (between waitMin and
 * waitMax seconds) between attempts
 */
public class RetryWithDelay implements Function<Flowable<Throwable>, Publisher<?>> {

    private static final Logger logger = LoggerFactory.getLogger(RetryWithDelay.class);

    private static final List<Predicate<Throwable>> DEFAULT_PREDICATES = Collections.singletonList(
            new ExceptionRetryPredicate(Exception.class));

    private final int waitMin;
    private final int waitMax;
    private final int maxTries;
    private final List<Predicate<Throwable>> retryPredicates;

    /**
     * Create a new instance
     * @param maxTries the maximum number of attempts
     * @param waitMin the minimum amount of time in seconds to wait between attempts
     * @param waitMax the maximum amount of time in seconds to wait between attempts
     * @param retryPredicates the checks to run to see if an error should be retried
     */
    private RetryWithDelay(int maxTries, int waitMin, int waitMax, List<Predicate<Throwable>> retryPredicates) {
        if (waitMin >= waitMax)
            throw new IllegalArgumentException("waitMin must be less than waitMax");
        this.maxTries = maxTries;
        this.waitMin = waitMin;
        this.waitMax = waitMax;
        this.retryPredicates = new ArrayList<>(Objects.requireNonNull(retryPredicates));
    }

    /**
     * @return a Builder for creating new instances
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Publisher<?> apply(Flowable<Throwable> errors) {
        return errors
                .flatMap(error -> {
                    boolean retryable = isRetryableException(error);
                    if (retryable)
                        logger.info("Retryable operation failed with exception: {}", error.getClass());
                    logger.debug("Operation exception", error);
                    return retryable ? Flowable.just(error) : Flowable.error(error);
                }).zipWith(Flowable.range(1, maxTries), (t, i) -> new Retry(i, t))
                .flatMap(retry -> retry.retry < maxTries ?
                        Flowable.timer(randomWait(), TimeUnit.SECONDS) : Flowable.error(retry.throwable))
                .doOnError(e -> logger.debug("Not retrying after error"));
    }

    private long randomWait() {
        return ThreadLocalRandom.current().nextInt(waitMin, waitMax);
    }

    /**
     * Check if an exception should be retried
     * @param e the exception
     * @return if it should be retried
     */
    private boolean isRetryableException(Throwable e) {
        boolean retryable = false;
        for (Predicate<Throwable> retryPredicate : retryPredicates) {
            if (retryPredicate.test(e)) {
                retryable = true;
                break;
            }
        }
        return retryable;
    }

    public static class Builder {

        private int waitMin = 30;
        private int waitMax = 36;
        private int maxRetries = 5;
        private List<Predicate<Throwable>> retryPredicates;

        /**
         * Set the maximum number of retry attempts, by default a request is retried 5 times
         * @param retries the maximum retry attempts
         * @return this builder
         */
        public Builder retries(int retries) {
            if (retries < 0)
                throw new IllegalArgumentException("Retries cannot be negative");
            maxRetries = retries;
            return this;
        }

        /**
         * Set the minimum number of seconds to wait between attempts, default value is 30
         * @param waitMin the min seconds, must be greater than 0 and less than waitMax
         * @return this builder
         */
        public Builder waitMin(int waitMin) {
            if (waitMin < 1)
                throw new IllegalArgumentException("waitMin cannot be less than 1");
            this.waitMin = waitMin;
            return this;
        }

        /**
         * Set the maximum number of seconds to wait between attempts, default value is 36
         * @param waitMax the max seconds, must be greater than 0 and waitMin
         * @return this builder
         */
        public Builder waitMax(int waitMax) {
            if (waitMax < 1)
                throw new IllegalArgumentException("waitMax cannot be less than 1");
            this.waitMax = waitMax;
            return this;
        }

        /**
         * Add an exception class to retry. If no exceptions or predicates are added, by default all exceptions are
         * retried
         * @param clazz the exception class to retry
         * @return this builder
         */
        public Builder exception(Class<? extends Exception> clazz) {
            return predicate(new ExceptionRetryPredicate(clazz));
        }

        /**
         * Add a predicate to retry. If no exceptions or predicates are added, by default all exceptions are retried
         * @param predicate the predicate that checks the exception and returns whether it should be retried
         * @return this builder
         */
        public Builder predicate(Predicate<Throwable> predicate) {
            if (retryPredicates == null)
                retryPredicates = new ArrayList<>();
            retryPredicates.add(predicate);
            return this;
        }

        public RetryWithDelay build() {
            if (retryPredicates == null)
                retryPredicates = DEFAULT_PREDICATES;
            return new RetryWithDelay(maxRetries + 1, waitMin, waitMax, retryPredicates);
        }

        private Builder() {

        }
    }

    private static class Retry {
        final int retry;
        final Throwable throwable;

        private Retry(int retry, Throwable throwable) {
            this.retry = retry;
            this.throwable = throwable;
        }
    }

    private static class ExceptionRetryPredicate implements Predicate<Throwable> {

        private final Class<? extends Throwable> clazz;

        ExceptionRetryPredicate(Class<? extends Throwable> clazz) {
            this.clazz = Objects.requireNonNull(clazz);
        }

        @Override
        public boolean test(Throwable throwable) {
            return clazz.isAssignableFrom(throwable.getClass());
        }
    }
}
