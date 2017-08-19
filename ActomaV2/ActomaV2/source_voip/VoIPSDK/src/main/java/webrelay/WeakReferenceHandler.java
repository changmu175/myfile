package webrelay;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 15/5/21.
 */
public abstract class WeakReferenceHandler<T> extends Handler
{
    private WeakReference<T> mReference;

    public WeakReferenceHandler(T reference)
    {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg)
    {
        if (mReference.get() == null)
            return;
        handleMessage(mReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);
}
