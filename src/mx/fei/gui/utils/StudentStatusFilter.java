package mx.fei.gui.utils;

import com.mysql.cj.log.Log;
import mx.fei.logic.dao.PracticeDAO;
import mx.fei.logic.dto.Practice;
import mx.fei.logic.dto.PracticeStatus;
import mx.fei.logic.dto.Student;
import mx.fei.logic.exceptions.DataOperationException;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StudentStatusFilter {

    public static final String ALL_LABEL = "Todos";
    private static final int PASSING_GRADE = 6;
    private static final Logger LOGGER = Logger.getLogger(StudentStatusFilter.class.getName());

    private StudentStatusFilter() {
    }

    public static List<String> filterLabels() {
        List<String> labels = new ArrayList<>();
        labels.add(ALL_LABEL);
        for (PracticeStatus status : PracticeStatus.values()) {
            labels.add(status.getLabel());
        }
        return labels;
    }

    public static PracticeStatus resolveStatus(Student student, Set<Integer> concludedStudentIds, Set<Integer> enrolledStudentIds) {
        PracticeStatus status = null;
        PracticeDAO practiceDAO = new PracticeDAO();
        try {
            Practice practice = practiceDAO.getPracticeByEnrollment(student.getEnrollment());
            float grade = practice.getGrade();
            if (grade >= PASSING_GRADE) {
                status = PracticeStatus.APROBADO;
            } else if (grade > 0) {
                status = PracticeStatus.REPROBADO;
            } else if (concludedStudentIds.contains(student.getUserId())) {
                status = PracticeStatus.CONCLUIDA;
            } else if (enrolledStudentIds.contains(student.getUserId())) {
                status = PracticeStatus.EN_CURSO;
            }
        } catch (DataOperationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return status;
    }

    public static List<Student> filterByStatus(List<Student> students, PracticeStatus status, Set<Integer> concludedStudentIds, Set<Integer> enrolledStudentIds) {
        List<Student> filteredStudents = new ArrayList<>();
        for (Student student : students) {
            if (status == null || resolveStatus(student, concludedStudentIds, enrolledStudentIds) == status) {
                filteredStudents.add(student);
            }
        }
        return filteredStudents;
    }
}
