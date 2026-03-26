package mx.fei.logic.idao;

import mx.fei.logic.dto.Student;

public interface IDAOStudent {
    public Student getStudentByEnrollment(String enrollment);
    public boolean registerStudent(Student student);
    public boolean modifyStudent(Student student);
   //public void choseProject();
    //public void createReport();
    //public void addReport();
    //public void createSelfEvaluation();
    //public void addSelfEvaluation();
    //public void uploadInitialFormats();
}
