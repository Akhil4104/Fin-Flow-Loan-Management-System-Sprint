package com.finflow.admin_service.idempotency;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class IdempotencyService {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(5);
    private static final long WAIT_INTERVAL_MILLIS = 100L;

    private final IdempotencyRecordRepository repository;

    public IdempotencyService(IdempotencyRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> checkIfExists(String key) {
        return repository.findByIdempotencyKey(key);
    }

    @Transactional
    public IdempotencyRecord reserveKey(String key) {
        Optional<IdempotencyRecord> existing = repository.findByIdempotencyKey(key);
        if (existing.isPresent()) {
            return existing.get();
        }

        IdempotencyRecord record = new IdempotencyRecord();
        record.setIdempotencyKey(key);

        try {
            return repository.saveAndFlush(record);
        } catch (DataIntegrityViolationException ex) {
            return repository.findByIdempotencyKey(key).orElseThrow(() -> ex);
        }
    }

    @Transactional
    public void saveResponse(String key, String responseBody, int statusCode) {
        IdempotencyRecord record = repository.findByIdempotencyKey(key)
                .orElseThrow(() -> new IllegalStateException("Missing reserved idempotency key: " + key));
        record.setResponseBody(responseBody);
        record.setStatusCode(statusCode);
        repository.save(record);
    }

    @Transactional(readOnly = true)
    public Optional<IdempotencyRecord> waitForCompletedRecord(String key) {
        Instant deadline = Instant.now().plus(WAIT_TIMEOUT);
        while (Instant.now().isBefore(deadline)) {
            Optional<IdempotencyRecord> record = repository.findByIdempotencyKey(key)
                    .filter(existing -> existing.getStatusCode() != null);
            if (record.isPresent()) {
                return record;
            }

            try {
                Thread.sleep(WAIT_INTERVAL_MILLIS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        return repository.findByIdempotencyKey(key)
                .filter(existing -> existing.getStatusCode() != null);
    }
}
