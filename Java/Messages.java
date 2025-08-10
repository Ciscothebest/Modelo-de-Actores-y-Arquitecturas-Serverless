// Messages.java
package com.example.akka; // Puedes ajustar el paquete si lo deseas

public class Messages {

    public static class ProcessTask {
        private final String taskData;

        public ProcessTask(String taskData) {
            this.taskData = taskData;
        }

        public String getTaskData() {
            return taskData;
        }
    }

    public static class TaskResult {
        private final String result;

        public TaskResult(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }

    public static class WorkerFailed {
        private final String workerId;
        private final String errorMessage;

        public WorkerFailed(String workerId, String errorMessage) {
            this.workerId = workerId;
            this.errorMessage = errorMessage;
        }

        public String getWorkerId() {
            return workerId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
