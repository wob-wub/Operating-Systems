// Types of requests that a process can send to Kernel.
public enum KernelMessageType {
   createProcess,
   locate,
   reschedule,
   exit
}