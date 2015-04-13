/*
 * Copyright (c) 2015 Mnubo. Released under MIT License.
 *
 *     Permission is hereby granted, free of charge, to any person obtaining a copy
 *     of this software and associated documentation files (the "Software"), to deal
 *     in the Software without restriction, including without limitation the rights
 *     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *     copies of the Software, and to permit persons to whom the Software is
 *     furnished to do so, subject to the following conditions:
 *
 *     The above copyright notice and this permission notice shall be included in
 *     all copies or substantial portions of the Software.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *     THE SOFTWARE.
 */

package com.mnubo.platform.android.sdk.internal.tasks.impl;

import android.util.Log;

import com.mnubo.platform.android.sdk.Strings;
import com.mnubo.platform.android.sdk.exceptions.MnuboException;
import com.mnubo.platform.android.sdk.internal.tasks.MnuboResponse;
import com.mnubo.platform.android.sdk.internal.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.mnubo.platform.android.sdk.Strings.SDK_ERROR_EXECUTING_TASK;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        Task.class,
        Log.class
})
public class TaskImplTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(Log.class);
    }

    @Test
    public void testExecute() throws Exception {

        final Task<Boolean> task = new DummyTask(false, null);

        MnuboResponse<Boolean> response = task.executeSync();
        assertThat(response.getResult(), equalTo(true));
        assertThat(response.getError(), is(nullValue()));
    }


    @Test
    public void testExecuteWithMnuboException() throws Exception {
        final MnuboException thrownException = new MnuboException("mnubo exception");
        when(Log.e(eq(DummyTask.class.getName()), eq(SDK_ERROR_EXECUTING_TASK), eq(thrownException))).thenReturn(0);

        final Task<Boolean> task = new DummyTask(true, thrownException);

        MnuboResponse<Boolean> response = task.executeSync();
        assertThat(response.getResult(), is(nullValue()));
        assertThat(response.getError(), is(equalTo(thrownException)));

    }


    @Test
    public void testExecuteWithGenericException() throws Exception {
        final RuntimeException thrownException = new RuntimeException("Other kind of exception");
        when(Log.e(eq(DummyTask.class.getName()), eq(SDK_ERROR_EXECUTING_TASK), Matchers.any(MnuboException.class))).thenReturn(0);

        final Task<Boolean> task = new DummyTask(true, thrownException);

        MnuboResponse<Boolean> response = task.executeSync();
        assertThat(response.getResult(), is(nullValue()));
        assertThat(response.getError(), is(any(MnuboException.class)));
    }

    private class DummyTask extends TaskImpl<Boolean> {
        final Boolean fails;
        final RuntimeException thrownException;

        private DummyTask(Boolean fails, RuntimeException thrownException) {
            super(null);
            this.fails = fails;
            this.thrownException = thrownException;
        }

        @Override
        protected Boolean executeMnuboCall() {
            if (fails) {
                throw thrownException;
            }
            return true;
        }
    }
}