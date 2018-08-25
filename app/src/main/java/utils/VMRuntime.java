package utils;


public final class VMRuntime {
    private static final VMRuntime THE_ONE = new VMRuntime();

    private VMRuntime() {
    }

    public static VMRuntime getRuntime() {
        return THE_ONE;
    }

    public native String[] properties();

    public native String bootClassPath();

    public native String classPath();

    public native String vmVersion();

    public native float getTargetHeapUtilization();

    public float setTargetHeapUtilization(float newTarget) {
        if (newTarget <= 0.0f || newTarget >= 1.0f) {
            throw new IllegalArgumentException(newTarget +
                    " out of range (0,1)");
        }

        synchronized (this) {
            float oldTarget = getTargetHeapUtilization();
            nativeSetTargetHeapUtilization(newTarget);
            return oldTarget;
        }
    }

    public native void setTargetSdkVersion(int targetSdkVersion);

    @Deprecated
    public long getMinimumHeapSize() {
        return 0;
    }

    @Deprecated
    public long setMinimumHeapSize(long size) {
        return 0;
    }

    @Deprecated
    public void gcSoftReferences() {}

    @Deprecated
    public void runFinalizationSync() {
        System.runFinalization();
    }

    private native void nativeSetTargetHeapUtilization(float newTarget);

    @Deprecated
    public boolean trackExternalAllocation(long size) {
        return true;
    }

    @Deprecated
    public void trackExternalFree(long size) {}

    @Deprecated
    public long getExternalBytesAllocated() {
        return 0;
    }

    public native void startJitCompilation();

    public native void disableJitCompilation();

    public native Object newNonMovableArray(Class<?> componentType, int length);

    public native long addressOf(Object array);

    public native void clearGrowthLimit();

    public native boolean isDebuggerActive();
}