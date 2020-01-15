package com.dicoding.academies.data

import com.dicoding.academies.data.source.local.entity.ContentEntity
import com.dicoding.academies.data.source.local.entity.CourseEntity
import com.dicoding.academies.data.source.local.entity.ModuleEntity
import com.dicoding.academies.data.source.remote.RemoteDataSource
import java.util.*

class AcademyRepository private constructor(private val remoteDataSource: RemoteDataSource) : AcademyDataSource {

    companion object {
        @Volatile
        private var instance: AcademyRepository? = null

        fun getInstance(remoteData: RemoteDataSource): AcademyRepository =
                instance ?: synchronized(this) {
                    instance ?: AcademyRepository(remoteData)
                }
    }

    override fun getAllCourses(): ArrayList<CourseEntity> {
        val courseResponses = remoteDataSource.getAllCourses()
        val courseList = ArrayList<CourseEntity>()
        for (response in courseResponses) {
            val course = CourseEntity(response.id,
                    response.title,
                    response.description,
                    response.date,
                    false,
                    response.imagePath)

            courseList.add(course)
        }
        return courseList
    }


    override fun getBookmarkedCourses(): ArrayList<CourseEntity> {
        val courseList = ArrayList<CourseEntity>()
        val courses = remoteDataSource.getAllCourses()
        for (response in courses) {
            val course = CourseEntity(response.id,
                    response.title,
                    response.description,
                    response.date,
                    false,
                    response.imagePath)
            courseList.add(course)
        }
        return courseList
    }

    // Pada metode ini di modul selanjutnya akan mengembalikan kelas POJO baru, gabungan antara course dengan module-nya.
    override fun getCourseWithModules(courseId: String): CourseEntity {
        lateinit var course: CourseEntity
        val courses = remoteDataSource.getAllCourses()
        for (response in courses) {
            if (response.id == courseId) {
                course = CourseEntity(response.id,
                        response.title,
                        response.description,
                        response.date,
                        false,
                        response.imagePath)
            }
        }
        return course
    }

    override fun getAllModulesByCourse(courseId: String): ArrayList<ModuleEntity> {
        val moduleList = ArrayList<ModuleEntity>()
        val moduleResponses = remoteDataSource.getModules(courseId)
        for(response in moduleResponses) {
            val course = ModuleEntity(response.moduleId,
                    response.courseId,
                    response.title,
                    response.position,
                    false)

            moduleList.add(course)
        }
        return moduleList
    }


    override fun getContent(courseId: String, moduleId: String): ModuleEntity {
        val moduleResponses = remoteDataSource.getModules(courseId)
        lateinit var module: ModuleEntity
        for(response in moduleResponses) {
            val id = response.moduleId
            if (id == moduleId) {
                module = ModuleEntity(id, response.courseId, response.title, response.position, false)
                module.contentEntity = ContentEntity(remoteDataSource.getContent(moduleId).content)
                break
            }
        }
        return module
    }
}
