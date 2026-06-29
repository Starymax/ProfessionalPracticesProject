package mx.fei.gui.utils;

import mx.fei.logic.dto.PracticeStatus;
import mx.fei.logic.dto.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class StudentStatusFilter {

    public static final String ALL_LABEL = "Todos";
    private static final int PASSING_GRADE = 6;

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
        float grade = student.getGrade();
        if (grade >= PASSING_GRADE) {
            status = PracticeStatus.APROBADO;
        } else if (grade > 0) {
            status = PracticeStatus.REPROBADO;
        } else if (concludedStudentIds.contains(student.getUserId())) {
            status = PracticeStatus.CONCLUIDA;
        } else if (enrolledStudentIds.contains(student.getUserId())) {
            status = PracticeStatus.EN_CURSO;
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
