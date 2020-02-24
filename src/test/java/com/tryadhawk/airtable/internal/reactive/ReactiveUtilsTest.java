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
                .test().assertNotComplete();
        verify(future, never()).get();
    }
}
