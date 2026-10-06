// States Kernel uses to keep track of whether a user process can run.
public enum ProcessState {
    Runnable,
    InboxEmpty,
    OutboxFull,
    QuantumExpired
}