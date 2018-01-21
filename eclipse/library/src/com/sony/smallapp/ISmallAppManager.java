/*
 * Copyright 2016 Pranav Pandey
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

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;


/**
 * An interface to launch small app on below API v2 (ICS) devices.
 */
public abstract interface ISmallAppManager extends IInterface {

    public abstract void hide() throws RemoteException;

    public abstract void startApplication(Intent intent) throws RemoteException;

    public static abstract class Stub extends Binder implements ISmallAppManager {

        static final int TRANSACTION_hide = 2;
        static final int TRANSACTION_startApplication = 1;

        public Stub() {
            attachInterface(this, "com.sony.smallapp.ISmallAppManager");
        }

        public static ISmallAppManager asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }

            IInterface iInterface = iBinder.queryLocalInterface(
                    "com.sony.smallapp.ISmallAppManager");
            if (iInterface != null && iInterface instanceof ISmallAppManager) {
                return (ISmallAppManager)iInterface;
            }
            return new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int integer1, Parcel parcel1, Parcel parcel2, int integer2)
                throws RemoteException {
            switch (integer1) {
                default:
                    return super.onTransact(integer1, parcel1, parcel2, integer2);

                case 1598968902:
                    parcel2.writeString("com.sony.smallapp.ISmallAppManager");
                    return true;

                case 1:
                    parcel1.enforceInterface("com.sony.smallapp.ISmallAppManager");
                    if (parcel1.readInt() != 0) { }

                    for (Intent localIntent = (Intent) Intent.CREATOR.createFromParcel(parcel1);;) {
                        startApplication(localIntent);
                        parcel2.writeNoException();
                        return true;
                    }
            }
        }

        /**
         * An interface to startApplication.
         */
        private static class Proxy implements ISmallAppManager {

            private IBinder mRemote;

            Proxy(IBinder paramIBinder) {
                this.mRemote = paramIBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void hide() throws RemoteException {
                Parcel parcel1 = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel1.writeInterfaceToken("com.sony.smallapp.ISmallAppManager");
                    this.mRemote.transact(2, parcel1, parcel2, 0);
                    parcel2.readException();
                    return;
                } finally {
                    parcel2.recycle();
                    parcel1.recycle();
                }
            }

            /* Error */
            public void startApplication(Intent paramIntent) throws RemoteException { }
        }
    }
}
