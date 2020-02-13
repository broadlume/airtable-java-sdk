package com.sybit.airtable.internal.reactive;

import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;
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
