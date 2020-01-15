package com.dicoding.academies.data;

import androidx.annotation.NonNull;

import com.dicoding.academies.data.source.local.entity.ContentEntity;
import com.dicoding.academies.data.source.local.entity.CourseEntity;
import com.dicoding.academies.data.source.local.entity.ModuleEntity;
import com.dicoding.academies.data.source.remote.RemoteDataSource;
import com.dicoding.academies.data.source.remote.response.CourseResponse;
import com.dicoding.academies.data.source.remote.response.ModuleResponse;

import java.util.ArrayList;
import java.util.List;

public class AcademyRepository implements AcademyDataSource {

    private volatile static AcademyRepository INSTANCE = null;

    private final RemoteDataSource remoteDataSource;

    private AcademyRepository(@NonNull RemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public static AcademyRepository getInstance(RemoteDataSource remoteData) {
        if (INSTANCE == null) {
            synchronized (AcademyRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AcademyRepository(remoteData);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public ArrayList<CourseEntity> getAllCourses() {
        List<CourseResponse> courseResponses = remoteDataSource.getAllCourses();
        ArrayList<CourseEntity> courseList = new ArrayList<>();
        for (CourseResponse response : courseResponses) {
            CourseEntity course = new CourseEntity(response.getId(),
                    response.getTitle(),
                    response.getDescription(),
                    response.getDate(),
                    false,
                    response.getImagePath());

            courseList.add(course);
        }
        return courseList;
    }

    // Pada metode ini di modul selanjutnya akan mengembalikan kelas POJO baru, gabungan antara course dengan module-nya.
    @Override
    public CourseEntity getCourseWithModules(final String courseId) {
        CourseEntity course = null;
        List<CourseResponse> courses = remoteDataSource.getAllCourses();
        for (CourseResponse response : courses) {
            if (response.getId().equals(courseId)) {
                course = new CourseEntity(response.getId(),
                        response.getTitle(),
                        response.getDescription(),
                        response.getDate(),
                        false,
                        response.getImagePath());
            }
        }
        return course;
    }


    @Override
    public ArrayList<CourseEntity> getBookmarkedCourses() {
        ArrayList<CourseEntity> courseList = new ArrayList<>();
        List<CourseResponse> courses = remoteDataSource.getAllCourses();
        for (CourseResponse response : courses) {
            CourseEntity course = new CourseEntity(response.getId(),
                    response.getTitle(),
                    response.getDescription(),
                    response.getDate(),
                    false,
                    response.getImagePath());
            courseList.add(course);
        }
        return courseList;
    }

    @Override
    public ArrayList<ModuleEntity> getAllModulesByCourse(String courseId) {
        ArrayList<ModuleEntity> moduleList = new ArrayList<>();
        List<ModuleResponse> moduleResponses = remoteDataSource.getModules(courseId);
        for (ModuleResponse response : moduleResponses) {
            ModuleEntity course = new ModuleEntity(response.getModuleId(),
                    response.getCourseId(),
                    response.getTitle(),
                    response.getPosition(),
                    false);

            moduleList.add(course);
        }
        return moduleList;
    }


    @Override
    public ModuleEntity getContent(String courseId, String moduleId) {
        List<ModuleResponse> moduleResponses = remoteDataSource.getModules(courseId);
        ModuleEntity module = null;
        for (ModuleResponse response : moduleResponses) {
            String id = response.getModuleId();
            if (id.equals(moduleId)) {
                module = new ModuleEntity(id, response.getCourseId(), response.getTitle(), response.getPosition(), false);
                module.contentEntity = new ContentEntity(remoteDataSource.getContent(moduleId).getContent());
                break;
            }
        }
        return module;
    }
}
