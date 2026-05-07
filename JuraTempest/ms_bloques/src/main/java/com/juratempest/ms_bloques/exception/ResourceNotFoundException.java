package com.juratempest.ms_bloques.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }

    @RestControllerAdvice
    public class GlobalExceptionHandler{
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex){
            Map<String, Object> response = new HashMap<>();
            response.put("timestamp",LocalDateTime.now());
            response.put("error", ex.getMessage());
            response.put("status",404);
            response.put("juratempest", "Error de Bloques");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
