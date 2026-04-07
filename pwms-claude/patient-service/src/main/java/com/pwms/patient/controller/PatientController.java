package com.pwms.patient.controller;

import com.pwms.patient.exception.PatientAlreadyExistsException;
import com.pwms.patient.exception.PatientNotFoundException;
import com.pwms.patient.interfaces.PatientIntf;
import com.pwms.patient.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientIntf patientService;

    // POST /api/patients
    @PostMapping
    public ResponseEntity<Patient> addPatient(@RequestBody Patient patient)
            throws PatientAlreadyExistsException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patientService.addPatient(patient));
    }

    // GET /api/patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.findAllPatients());
    }

    // GET /api/patients/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable int id)
            throws PatientNotFoundException {
        return ResponseEntity.ok(patientService.findPatientById(id));
    }

    // GET /api/patients/search?name=Ashok
    @GetMapping("/search")
    public ResponseEntity<Patient> getPatientByName(@RequestParam String name)
            throws PatientNotFoundException {
        return ResponseEntity.ok(patientService.findPatientByName(name));
    }

    // PUT /api/patients/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable int id,
            @RequestBody Patient updatedPatient) throws PatientNotFoundException {
        return ResponseEntity.ok(patientService.updatePatientById(id, updatedPatient));
    }

    // DELETE /api/patients/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(@PathVariable int id)
            throws PatientNotFoundException {
        patientService.deletePatientById(id);
        return ResponseEntity.ok("Patient with id " + id + " deleted successfully.");
    }
}