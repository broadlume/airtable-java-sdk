package com.tryadhawk.airtable.internal.reactive;

import java.util.concurrent.ExecutionException;
import org.asynchttpclient.ListenableFuture;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class ReactiveUtilsTest {

    private ListenableFuture<String> future = mock(ListenableFuture.class);

    @Before
    public void setup() {
        when(future.addListener(any(), isNull())).then(invocation -> {
            Runnable listener = invocation.getArgument(0);
            listener.run();
            return null;
        });
    }

    /**
     * Should convert the ListenableFuture to a Single and properly signal the result
     */
    @Test
    public void toSingleTest() throws InterruptedException, ExecutionException {
        when(future.get()).thenReturn("test-val");
        ReactiveUtils.fromFuture(() -> future)
                .test().await()
                .assertResult("test-val");
    }

    /**
     * Should not call the future's get method if the future is cancelled
     */
    @Test
    public void toSingleCancelledTest() throws ExecutionException, InterruptedException {
        when(future.isCancelled()).thenReturn(true);
        ReactiveUtils.fromFuture(() -> future)
                .test().cancel();
        verify(future, never()).get();
    }
}
