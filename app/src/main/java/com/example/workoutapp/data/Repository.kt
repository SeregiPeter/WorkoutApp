package com.example.workoutapp.data

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class Repository(
    private val workoutApiService: WorkoutApiService,
    private val overpassApiService: OverpassApiService,
    private val challengeResultDao: ChallengeResultDao,
    private val workoutDao: WorkoutDao
    ) {
    suspend fun getExercises(): Result<List<Exercise>> {
        return try {
            Result.Success(workoutApiService.getExercises())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getExercise(id: Int): Result<Exercise> {
        return try {
            Result.Success(workoutApiService.getExercise(id))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    suspend fun getCategories(): Result<List<Category>> {
        return try {
            Result.Success(workoutApiService.getCategories())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    suspend fun getWorkouts(): Result<List<Workout>> {
        return try {
            val remoteWorkouts = workoutApiService.getWorkouts().map { it.copy(remote = true) }

            val localWorkouts = workoutDao.getWorkoutsWithExercises().mapNotNull { workoutWithExercises ->
                try {
                    workoutWithExercises.toWorkout { exerciseId ->
                        workoutApiService.getExercise(exerciseId)
                    }
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        workoutDao.deleteWorkout(workoutWithExercises.workout.id)
                    }
                    null
                } catch (e: Exception) {
                    null
                }
            }

            Result.Success(remoteWorkouts + localWorkouts)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun getWorkout(id: Int, remote: Boolean): Result<Workout> {
        return try {
            val workout = if (remote) {
                workoutApiService.getWorkout(id).copy(remote = true)
            } else {
                val workoutWithExercises = workoutDao.getWorkoutById(id)
                if (workoutWithExercises == null) {
                    return Result.Error(IllegalStateException("Workout not found in local DB."))
                }

                try {
                    workoutWithExercises.toWorkout { exerciseId ->
                        workoutApiService.getExercise(exerciseId)
                    }
                } catch (e: HttpException) {
                    if (e.code() == 404) {
                        workoutDao.deleteWorkout(id)
                        return Result.Error(IllegalStateException("Workout removed due to missing Exercise."))
                    } else {
                        throw e
                    }
                }
            }

            Result.Success(workout)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun createWorkout(workoutRequest: WorkoutCreateRequest): Result<Workout> {
        return try {
            val workoutEntity = WorkoutEntity(name = workoutRequest.name, remote = false)
            val exerciseEntities = workoutRequest.exercises.map { we ->
                WorkoutExerciseEntity(
                    workoutId = 0, // DAO fogja beállítani
                    exerciseId = we.exercise_id,
                    sets = we.sets,
                    reps = we.reps,
                    duration = we.duration,
                    rest_time_between = we.rest_time_between,
                    rest_time_after = we.rest_time_after
                )
            }

            val workoutId = workoutDao.insertWorkoutWithExercises(workoutEntity, exerciseEntities).toInt()

            val exercises = exerciseEntities.mapNotNull { we ->
                workoutApiService.getExercise(we.exerciseId)?.let { exercise ->
                    we.toWorkoutExercise(exercise)
                }
            }

            Result.Success(
                Workout(
                    id = workoutId,
                    name = workoutRequest.name,
                    exercises = exercises,
                    remote = false
                )
            )
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun deleteWorkout(workoutId: Int): Result<Unit> {
        return try {
            workoutDao.deleteWorkout(workoutId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getChallenges(): Result<List<Challenge>> {
        return try {
            val challenges = workoutApiService.getChallenges()
            Result.Success(challenges)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getChallenge(id: Int): Result<Challenge> {
        return try {
            val challenge = workoutApiService.getChallenge(id)
            Result.Success(challenge)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveChallengeResult(result: ChallengeResultEntity): Result<Unit> {
        return try {
            challengeResultDao.insertResult(result)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getChallengeResults(challengeId: Int): Result<List<ChallengeResultEntity>> {
        return try {
            val results = challengeResultDao.getResultsForChallenge(challengeId)
            Result.Success(results)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    suspend fun fetchFitnessStations(lat: Double, lon: Double, radius: Int = 5000): Result<List<Element>> {
        return try {
            val query = buildOverpassQuery(lat, lon, radius)
            val body = query.toRequestBody("application/x-www-form-urlencoded".toMediaType())
            val response = overpassApiService.getFitnessStations(body)
            if (response.isSuccessful) {
                val elements = response.body()?.elements.orEmpty()
                Result.Success(elements)
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String {
        return """
        [out:json];
        (
          node["leisure"="fitness_station"](around:$radius,$lat,$lon);
          way["leisure"="fitness_station"](around:$radius,$lat,$lon);
        );
        out center;
    """.trimIndent()
    }
}
