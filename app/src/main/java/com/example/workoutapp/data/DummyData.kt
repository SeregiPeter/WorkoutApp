package com.example.workoutapp.data

val dummyCategories = listOf(
    Category(1, "Strength", "Exercises focused on building muscle strength"),
    Category(2, "Cardio", "Exercises to improve heart rate and endurance"),
    Category(3, "Mobility", "Exercises for flexibility and joint movement")
)

val dummyExercises = listOf(
    Exercise(1, "Push-Up", "A basic upper body exercise using bodyweight.", "https://www.youtube.com/embed/IODxDxX7oi4", "https://cdn-icons-png.flaticon.com/512/2548/2548536.png" , false, dummyCategories[0]),
    Exercise(2, "Squat", "A lower body exercise to strengthen legs and glutes.", "https://www.youtube.com/embed/aclHkVaku9U", "https://cdn-icons-png.flaticon.com/512/3043/3043271.png", false, dummyCategories[0]),
    Exercise(3, "Jump Rope", "A great cardio exercise for endurance.", "https://www.youtube.com/embed/1BZM9g2RWhE", "https://cdn-icons-png.flaticon.com/512/5146/5146932.png", true, dummyCategories[1]),
    Exercise(4, "Plank", "Core stability exercise.", "https://www.youtube.com/embed/pSHjTRCQxIw", "https://cdn-icons-png.flaticon.com/512/3043/3043240.png", true, dummyCategories[0]),
    Exercise(5, "Lunges", "Strengthens legs and improves balance.", "https://www.youtube.com/embed/QOVaHwm-Q6U", "https://cdn-icons-png.flaticon.com/512/3043/3043233.png", false, dummyCategories[0])
)

val dummyWorkoutExercises = listOf(
    WorkoutExercise(1, "Push-Up", "Upper body strength", "https://www.youtube.com/embed/IODxDxX7oi4", "https://cdn-icons-png.flaticon.com/512/2548/2548536.png", false, sets = 3, reps = 15, duration = null, rest_time_between = 5, rest_time_after = 10),
    WorkoutExercise(2, "Squat", "Lower body strength", "https://www.youtube.com/embed/aclHkVaku9U", "https://cdn-icons-png.flaticon.com/512/3043/3043271.png", false, sets = 3, reps = 20, duration = null, rest_time_between = 5, rest_time_after = 10),
    WorkoutExercise(3, "Jump Rope", "Cardio endurance", "https://www.youtube.com/embed/1BZM9g2RWhE", "https://cdn-icons-png.flaticon.com/512/5146/5146932.png", true, sets = 3, reps = null, duration = 15, rest_time_between = 5, rest_time_after = 10),
    WorkoutExercise(4, "Plank", "Core stability", "https://www.youtube.com/embed/pSHjTRCQxIw", "https://cdn-icons-png.flaticon.com/512/3043/3043240.png", true, sets = 3, reps = null, duration = 15, rest_time_between = 5, rest_time_after = 10)
)

var dummyWorkouts = listOf(
    Workout(
        id = 1,
        name = "Full Body Workout",
        exercises = listOf(dummyWorkoutExercises[0], dummyWorkoutExercises[1], dummyWorkoutExercises[2])
    ),
    Workout(
        id = 2,
        name = "Strength Training",
        exercises = listOf(dummyWorkoutExercises[0], dummyWorkoutExercises[1], dummyWorkoutExercises[3])
    ),
    Workout(
        id = 3,
        name = "Cardio Blast",
        exercises = listOf(dummyWorkoutExercises[2])
    )
)

val dummyChallenges = listOf(
    Challenge(
        id = 1,
        name = "The Squat Challenge",
        description = "Challenge yourself with squats.",
        count_reps = true,
        duration = 15,
        measurement_method = "downUpMovement",
        exercise = ExerciseShort(2, "Squat", "A lower body exercise to strengthen legs and glutes.", "https://www.youtube.com/embed/aclHkVaku9U", "https://cdn-icons-png.flaticon.com/512/3043/3043271.png", false)
    ),
    Challenge(
        id = 2,
        name = "The Push-up Challenge",
        description = "Challenge yourself with push-ups.",
        count_reps = true,
        duration = 20,
        measurement_method = "proximity",
        exercise = ExerciseShort(1, "Push-Up", "A basic upper body exercise using bodyweight.", "https://www.youtube.com/embed/IODxDxX7oi4", "https://cdn-icons-png.flaticon.com/512/2548/2548536.png" , false)
    ),
    Challenge(
        id = 3,
        name = "The Plank Challenge",
        description = "Challenge yourself with planks.",
        count_reps = false,
        duration = null,
        measurement_method = "stillness",
        exercise = ExerciseShort(4, "Plank", "Core stability exercise.", "https://www.youtube.com/embed/pSHjTRCQxIw", "https://cdn-icons-png.flaticon.com/512/3043/3043240.png", true)
    )
)

