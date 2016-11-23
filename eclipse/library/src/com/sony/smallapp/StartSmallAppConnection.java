/*
 * Copyright (C) 2016 Pranav Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sony.smallapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * Interface for monitoring the state of an small application service.
 */
public class StartSmallAppConnection implements ServiceConnection {

    /**
     * Component of the small app.
     */
    private ComponentName mCompName;

    /**
     * Context to start service.
     */
    private Context mContext;

    public StartSmallAppConnection(Context context, ComponentName componentName) {
        this.mCompName = componentName;
        this.mContext = context;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        ISmallAppManager iSmallAppManager = ISmallAppManager.Stub.asInterface(iBinder);

        try {
            Intent localIntent = new Intent("com.sony.smallapp.intent.action.MAIN");
            localIntent.setComponent(this.mCompName);

            if (iSmallAppManager != null) {
                iSmallAppManager.startApplication(localIntent);
            }
            mContext.unbindService(this);
            return;
        } catch (RemoteException remoteException) {
            remoteException.printStackTrace();
        }
    }

    public void onServiceDisconnected(ComponentName componentName) { }
}
