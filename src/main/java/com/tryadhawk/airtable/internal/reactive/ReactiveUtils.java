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

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.asynchttpclient.ListenableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for rxjava
 */
public class ReactiveUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUtils.class);

    public static <T> Single<T> fromFuture(Supplier<ListenableFuture<T>> supplier) {
        return Single.create((SingleEmitter<T> emitter) -> {
            ListenableFuture<T> future = supplier.get();
            future.addListener(() -> {
                if (!future.isCancelled()) {
                    try {
                        emitter.onSuccess(future.get());
                    } catch (Throwable t) {
                        if (t instanceof ExecutionException && t.getCause() != null) {
                            logger.debug("Unwrapping exception", t);
                            t = t.getCause();
                        }
                        emitter.onError(t);
                    }
                }
            }, null);
            emitter.setCancellable(() -> future.cancel(true));
        }).observeOn(Schedulers.computation());
    }

    private ReactiveUtils() { }
}
