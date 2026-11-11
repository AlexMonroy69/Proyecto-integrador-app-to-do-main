package co.Proyecto_Backendll.domain.model;

public class CourseModel {

    private Long courseId;
    private String code;
    private String name;
    private String description;
    private Integer maxCapacity;

    public CourseModel() {}

    public CourseModel(Long courseId, String code, String name, String description, Integer maxCapacity) {
        this.courseId = courseId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.maxCapacity = maxCapacity;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

}
