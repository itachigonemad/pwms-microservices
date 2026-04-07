package com.pwms.patient.service;

import com.pwms.patient.client.NotificationClient;
import com.pwms.patient.exception.PatientAlreadyExistsException;
import com.pwms.patient.exception.PatientNotFoundException;
import com.pwms.patient.interfaces.PatientIntf;
import com.pwms.patient.model.Patient;
import com.pwms.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService implements PatientIntf {

    private final PatientRepository  patientRepo;
    private final NotificationClient notificationClient; // HTTP client, not direct bean

    private static final int DEFAULT_ADMIN_ID = 1;

    @Override
    public Patient addPatient(Patient patient) throws PatientAlreadyExistsException {
        if (patientRepo.existsByEmail(patient.getEmail())) {
            throw new PatientAlreadyExistsException(
                    "Patient already exists with email: " + patient.getEmail());
        }
        Patient saved = patientRepo.save(patient);

        // HTTP call to notification-service
        notificationClient.notifyNewPatientRegistered(
                saved.getPatientId(), DEFAULT_ADMIN_ID);

        return saved;
    }

    @Override
    public Patient findPatientById(int patientId) throws PatientNotFoundException {
        return patientRepo.findById(patientId)
                .orElseThrow(() -> new PatientNotFoundException(
                        "Patient not found with id: " + patientId));
    }

    @Override
    public Patient findPatientByName(String patientName) throws PatientNotFoundException {
        Patient patient = patientRepo.findByPatientName(patientName);
        if (patient == null) {
            throw new PatientNotFoundException(
                    "Patient not found with name: " + patientName);
        }
        return patient;
    }

    @Override
    public Patient updatePatientById(int patientId, Patient updatedPatient)
            throws PatientNotFoundException {
        findPatientById(patientId);
        updatedPatient.setPatientId(patientId);
        return patientRepo.save(updatedPatient);
    }

    @Override
    public void deletePatientById(int patientId) throws PatientNotFoundException {
        if (!patientRepo.existsById(patientId)) {
            throw new PatientNotFoundException(
                    "Patient not found with id: " + patientId);
        }
        patientRepo.deleteById(patientId);
    }

    @Override
    public List<Patient> findAllPatients() {
        return patientRepo.findAll();
    }
}