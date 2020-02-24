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

import java.io.IOException;
import io.reactivex.rxjava3.core.Single;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RetryWithDelayTest {

    /**
     * Should retry failures until a success
     */
    @Test
    public void applyTest() throws InterruptedException {
        int[] tries = { 0 };
        Single<String> single = Single.just("test")
                .flatMap(s -> ++tries[0] < 4 ? Single.error(new RuntimeException()) : Single.just(s));

        single.retryWhen(RetryWithDelay.builder().waitMin(1).waitMax(2).retries(3).build())
                .test()
                .await()
                .assertResult("test");
        assertEquals(4, tries[0]);
    }

    /**
     * Should throw an exception if a failure occurs and the max retry count has been reached
     */
    @Test
    public void applyTooManyFailuresTest() throws InterruptedException {
        Single<String> single = Single.error(new IOException("test"));

        single.retryWhen(RetryWithDelay.builder().waitMin(1).waitMax(2).retries(2).build())
                .test()
                .await()
                .assertError(IOException.class);
    }

    /**
     * Should not retry at all if a failure occurs that is not retryable
     */
    @Test
    public void applyNotRetryableExceptionTest() throws InterruptedException {
        int[] tries = { 0 };
        Single<String> single = Single.just("test")
                .flatMap(s -> {
                    ++tries[0];
                    return Single.error(new IOException("test"));
                });

        single.retryWhen(RetryWithDelay.builder().exception(RuntimeException.class).build())
                .test()
                .await()
                .assertError(IOException.class);
        assertEquals(1, tries[0]);
    }

    /**
     * Should retry exceptions that are specifically set as retryable
     */
    @Test
    public void applyRetryableExceptionTest() throws InterruptedException {
        int[] tries = { 0 };
        Single<String> single = Single.just("test")
                .flatMap(s -> ++tries[0] < 2 ? Single.error(new IOException()) : Single.just(s));

        single.retryWhen(RetryWithDelay.builder().waitMin(1).waitMax(2).exception(IOException.class).build())
                .test()
                .await()
                .assertResult("test");
    }
}
