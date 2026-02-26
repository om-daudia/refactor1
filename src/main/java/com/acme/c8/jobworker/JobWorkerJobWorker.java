package com.acme.c8.jobworker;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.VariableAsType;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Job worker component for processing Camunda Zeebe jobs.
 * Handles user lookup and patient filtering tasks in the workflow.
 *
 * @since 1.0.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerJobWorker {

    private static final String FIND_USER_JOB_TYPE = "com.capbpm.c8.JobWorker.FindUser:v.1.1";
    private static final String FILTER_PATIENTS_JOB_TYPE = "com.capbpm.c8.JobWorker.filterPatients:v.1.1";
    private static final String ERROR_CODE = "ERR_CODE";
    private static final String DURATION_KEY = "duration";

    private final JobWorkerJobWorkerService service;

    public JobWorkerJobWorker(JobWorkerJobWorkerService service) {
        this.service = service;
    }

    /**
     * Finds a user by their ID.
     *
     * @param job the activated Zeebe job
     * @param userId the ID of the user to find
     * @return a map containing the result of the user lookup operation
     * @throws ZeebeBpmnError if an error occurs during processing
     * @throws NullPointerException if userId is null
     */
    @JobWorker(type = FIND_USER_JOB_TYPE, fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @VariableAsType String userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        log.debug("Finding user with ID: {}", userId);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.debug("User lookup completed successfully for ID: {}", userId);
            return outputs;
        } catch (Exception e) {
            log.error("Error finding user with ID: {}", userId, e);
            throw new ZeebeBpmnError(ERROR_CODE, e.getMessage(), job.getVariablesAsMap());
        }
    }

    /**
     * Filters patients based on the specified index.
     *
     * @param job the activated Zeebe job
     * @param index the index or page number for patient filtering
     * @return a map containing the duration of the filtering operation
     * @throws ZeebeBpmnError if an error occurs during processing
     * @throws NullPointerException if index is null
     */
    @JobWorker(type = FILTER_PATIENTS_JOB_TYPE, fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @VariableAsType Integer index) {
        Objects.requireNonNull(index, "index must not be null");

        log.debug("Starting patient filtering with index: {}", index);

        try {
            long duration = service.filterPatientsImpl(index);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put(DURATION_KEY, duration);

            log.debug("Patient filtering completed in {} milliseconds", duration);
            return outputs;
        } catch (Exception e) {
            log.error("Error filtering patients with index: {}", index, e);
            throw new ZeebeBpmnError(ERROR_CODE, e.getMessage(), job.getVariablesAsMap());
        }
    }
}


