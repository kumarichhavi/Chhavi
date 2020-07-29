package androidx.core.app;

import org.ligi.tracedroid.logging.Log;

/**
 * Mitigates the effect of a SecurityException which can occur in {@link JobIntentService}.
 * The issue is tracked here: https://issuetracker.google.com/issues/63622293
 */
public abstract class SafeJobIntentService extends JobIntentService {

    @Override
    GenericWorkItem dequeueWork() {
        try {
            return super.dequeueWork();
        } catch (SecurityException e) {
            // There is not much we can do here.
            Log.e(getClass().getSimpleName(), e);
        }
        return null;
    }

}
