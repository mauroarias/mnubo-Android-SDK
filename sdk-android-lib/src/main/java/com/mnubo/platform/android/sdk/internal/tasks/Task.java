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

package com.mnubo.platform.android.sdk.internal.tasks;

import android.util.Log;

import com.mnubo.platform.android.sdk.internal.connect.connection.MnuboConnectionManager;

import java.io.Serializable;

import static com.mnubo.platform.android.sdk.Strings.SDK_ERROR_EXECUTING_TASK;
import static com.mnubo.platform.android.sdk.api.MnuboApi.CompletionCallBack;
import static com.mnubo.platform.android.sdk.api.services.buffer.MnuboBufferService.FailedAttemptCallback;

public abstract class Task<Result> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final String TAG = this.getClass().getName();

    public MnuboResponse<Result> executeSync(MnuboConnectionManager connectionManager) {
        return executeSync(connectionManager, null);
    }

    public abstract MnuboResponse<Result> executeSync(MnuboConnectionManager connectionManager, FailedAttemptCallback failedCallback);

    public void executeAsync(MnuboConnectionManager connectionManager, CompletionCallBack<Result> callback) {
        this.executeAsync(connectionManager, callback, null);
    }

    public void executeAsync(MnuboConnectionManager connectionManager, CompletionCallBack<Result> callback, FailedAttemptCallback failedCallback) {
        AsyncTaskFactory.create(connectionManager, this, callback, failedCallback).execute();
    }

    protected void handleError(Exception ex, FailedAttemptCallback failedCallback) {
        if (ex != null) {
            Log.e(TAG, SDK_ERROR_EXECUTING_TASK, ex);

            if (failedCallback != null) {
                failedCallback.onFailure(this);
            }
        }
    }

    protected abstract Result executeMnuboCall(final MnuboConnectionManager connectionManager);
}