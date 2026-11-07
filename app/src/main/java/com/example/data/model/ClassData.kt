package com.example.data.model

data class Man1Class(
    val name: String,
    val grade: Int, // 10, 11, 12
    val code: String,
    val scheduleDay: String, // "Senin" or "Selasa"
    val scheduleTime: String = "09.30 - 10.00 & 12.00 - 12.30 WIB",
    val motto: String = "Bersih Lingkunganku, Unggul Madrasahku"
)

data class MentoringSchedule(
    val no: Int,
    val dayName: String, // "Senin" or "Selasa"
    val fullDate: String, // e.g. "Senin, 14 September"
    val mentors: String, // "Malya Adzillina Silmi, S.Si. & Masayu Nurul Ana, S.Ant., M.Pd."
    val announcer: String // "Nurokhmah, S.Pd., M.Pd."
)

object Man1ClassDirectory {
    val grade10Classes = listOf(
        "X A", "X B", "X C", "X D", "X E", "X F", "X G", "X PK 1", "X PK 2"
    )

    val grade11Classes = listOf(
        "XI A", "XI B", "XI C", "XI D", "XI E", "XI F", "XI G", "XI PK 1", "XI PK 2"
    )

    val grade12Classes = listOf(
        "XII A", "XII B", "XII C", "XII D", "XII E", "XII F", "XII G", "XII PK 1", "XII PK 2"
    )

    // Schedule per Image 2:
    // Senin: XE, XF, XG, XPK1, XPK2; XIA, XIB, XIC, XID; XIIA, XIIB, XIIC, XIID
    // Selasa: XA, XB, XC, XD; XIE, XIF, XIG, XIPK1, XIPK2; XIIE, XIIF, XIIG, XIIPK1, XIIPK2
    val seninClassNames = setOf(
        "X E", "X F", "X G", "X PK 1", "X PK 2",
        "XI A", "XI B", "XI C", "XI D",
        "XII A", "XII B", "XII C", "XII D"
    )

    val selasaClassNames = setOf(
        "X A", "X B", "X C", "X D",
        "XI E", "XI F", "XI G", "XI PK 1", "XI PK 2",
        "XII E", "XII F", "XII G", "XII PK 1", "XII PK 2"
    )

    val all27Classes: List<Man1Class> by lazy {
        val list = mutableListOf<Man1Class>()

        fun determineDay(name: String): String {
            return if (seninClassNames.contains(name)) "Senin" else "Selasa"
        }

        grade10Classes.forEach { name ->
            list.add(
                Man1Class(
                    name = name,
                    grade = 10,
                    code = "M1Y-10-${name.replace(" ", "")}",
                    scheduleDay = determineDay(name)
                )
            )
        }

        grade11Classes.forEach { name ->
            list.add(
                Man1Class(
                    name = name,
                    grade = 11,
                    code = "M1Y-11-${name.replace(" ", "")}",
                    scheduleDay = determineDay(name)
                )
            )
        }

        grade12Classes.forEach { name ->
            list.add(
                Man1Class(
                    name = name,
                    grade = 12,
                    code = "M1Y-12-${name.replace(" ", "")}",
                    scheduleDay = determineDay(name)
                )
            )
        }

        list
    }

    fun getAllClassNames(): List<String> = all27Classes.map { it.name }

    fun getClassByName(name: String): Man1Class? {
        return all27Classes.find { it.name.equals(name.trim(), ignoreCase = true) }
    }

    fun getScheduleForDay(dayName: String): List<Man1Class> {
        return all27Classes.filter { it.scheduleDay.equals(dayName.trim(), ignoreCase = true) }
    }

    fun getScheduleForClass(className: String): Man1Class? {
        return all27Classes.find { it.name.equals(className.trim(), ignoreCase = true) }
    }
}

object MentoringScheduleCatalog {
    // 21 official mentoring schedules from Image 3 (Jadwal Pendampingan Bank Sampah)
    val schedules = listOf(
        MentoringSchedule(
            no = 1,
            dayName = "Senin",
            fullDate = "Senin, 14 September",
            mentors = "Malya Adzillina Silmi, S.Si. & Masayu Nurul Ana, S.Ant., M.Pd.",
            announcer = "Nurokhmah, S.Pd., M.Pd."
        ),
        MentoringSchedule(
            no = 2,
            dayName = "Senin",
            fullDate = "Senin, 21 September",
            mentors = "Masayu Nurul Ana, S.Ant., M.Pd. & Purnami Nugraheni, S.Pd.",
            announcer = "Dimas Wahyu Saputra, A.Md."
        ),
        MentoringSchedule(
            no = 3,
            dayName = "Senin",
            fullDate = "Senin, 28 September",
            mentors = "Purnami Nugraheni, S.Pd. & Dina Wahyuningtyas, S.Pd.",
            announcer = "Ainun Nur Fadhila Syahban, S.Pd."
        ),
        MentoringSchedule(
            no = 4,
            dayName = "Selasa",
            fullDate = "Selasa, 29 September",
            mentors = "Dina Wahyuningtyas, S.Pd. & Epa Wira Darmawan, S.Pd.",
            announcer = "Purnami Nugraheni, S.Pd."
        ),
        MentoringSchedule(
            no = 5,
            dayName = "Senin",
            fullDate = "Senin, 5 Oktober",
            mentors = "Epa Wira Darmawan, S.Pd. & Andry Sunny",
            announcer = "Malya Adzillina Silmi, S.Si."
        ),
        MentoringSchedule(
            no = 6,
            dayName = "Selasa",
            fullDate = "Selasa, 6 Oktober",
            mentors = "Andry Sunny & Nurokhmah, S.Pd., M.Pd.",
            announcer = "Dina Wahyuningtyas, S.Pd."
        ),
        MentoringSchedule(
            no = 7,
            dayName = "Senin",
            fullDate = "Senin, 12 Oktober",
            mentors = "Nurokhmah, S.Pd., M.Pd. & Mohammad Ikhfan",
            announcer = "Epa Wira Darmawan, S.Pd."
        ),
        MentoringSchedule(
            no = 8,
            dayName = "Selasa",
            fullDate = "Selasa, 13 Oktober",
            mentors = "Mohammad Ikhfan & Ainun Nur Fadhila Syahban, S.Pd.",
            announcer = "Masayu Nurul Ana, S.Ant., M.Pd."
        ),
        MentoringSchedule(
            no = 9,
            dayName = "Senin",
            fullDate = "Senin, 19 Oktober",
            mentors = "Ainun Nur Fadhila Syahban, S.Pd. & Dananto",
            announcer = "Ismail Rozi Muslim Amir, Lc."
        ),
        MentoringSchedule(
            no = 10,
            dayName = "Selasa",
            fullDate = "Selasa, 20 Oktober",
            mentors = "Dananto & Dimas Wahyu Saputra, A.Md.",
            announcer = "Nurokhmah, S.Pd., M.Pd."
        ),
        MentoringSchedule(
            no = 11,
            dayName = "Senin",
            fullDate = "Senin, 26 Oktober",
            mentors = "Dimas Wahyu Saputra, A.Md. & Ismail Rozi Muslim Amir, Lc.",
            announcer = "Ainun Nur Fadhila Syahban, S.Pd."
        ),
        MentoringSchedule(
            no = 12,
            dayName = "Selasa",
            fullDate = "Selasa, 27 Oktober",
            mentors = "Ismail Rozi Muslim Amir, Lc. & Ariyanto, S.I.P.",
            announcer = "Dimas Wahyu Saputra, A.Md."
        ),
        MentoringSchedule(
            no = 13,
            dayName = "Senin",
            fullDate = "Senin, 2 November",
            mentors = "Ariyanto, S.I.P. & Malya Adzillina Silmi, S.Si.",
            announcer = "Purnami Nugraheni, S.Pd."
        ),
        MentoringSchedule(
            no = 14,
            dayName = "Selasa",
            fullDate = "Selasa, 3 November",
            mentors = "Malya Adzillina Silmi, S.Si. & Masayu Nurul Ana, S.Ant., M.Pd.",
            announcer = "Ariyanto, S.I.P."
        ),
        MentoringSchedule(
            no = 15,
            dayName = "Senin",
            fullDate = "Senin, 9 November",
            mentors = "Masayu Nurul Ana, S.Ant., M.Pd. & Purnami Nugraheni, S.Pd.",
            announcer = "Malya Adzillina Silmi, S.Si."
        ),
        MentoringSchedule(
            no = 16,
            dayName = "Selasa",
            fullDate = "Selasa, 10 November",
            mentors = "Purnami Nugraheni, S.Pd. & Dina Wahyuningtyas, S.Pd.",
            announcer = "Epa Wira Darmawan, S.Pd."
        ),
        MentoringSchedule(
            no = 17,
            dayName = "Senin",
            fullDate = "Senin, 16 November",
            mentors = "Dina Wahyuningtyas, S.Pd. & Epa Wira Darmawan, S.Pd.",
            announcer = "Masayu Nurul Ana, S.Ant., M.Pd."
        ),
        MentoringSchedule(
            no = 18,
            dayName = "Selasa",
            fullDate = "Selasa, 17 November",
            mentors = "Epa Wira Darmawan, S.Pd. & Andry Sunny",
            announcer = "Dina Wahyuningtyas, S.Pd."
        ),
        MentoringSchedule(
            no = 19,
            dayName = "Senin",
            fullDate = "Senin, 23 November",
            mentors = "Andry Sunny & Nurokhmah, S.Pd., M.Pd.",
            announcer = "Ismail Rozi Muslim Amir, Lc."
        ),
        MentoringSchedule(
            no = 20,
            dayName = "Selasa",
            fullDate = "Selasa, 24 November",
            mentors = "Nurokhmah, S.Pd., M.Pd. & Mohammad Ikhfan",
            announcer = "Ariyanto, S.I.P."
        ),
        MentoringSchedule(
            no = 21,
            dayName = "Senin",
            fullDate = "Senin, 30 November",
            mentors = "Mohammad Ikhfan & Ainun Nur Fadhila Syahban, S.Pd.",
            announcer = "Dimas Wahyu Saputra, A.Md."
        )
    )

    fun getSchedulesForDay(dayName: String): List<MentoringSchedule> {
        return schedules.filter { it.dayName.equals(dayName.trim(), ignoreCase = true) }
    }

    fun getUpcomingScheduleForClass(className: String): MentoringSchedule? {
        val studentClass = Man1ClassDirectory.getClassByName(className) ?: return schedules.firstOrNull()
        return schedules.firstOrNull { it.dayName.equals(studentClass.scheduleDay, ignoreCase = true) }
            ?: schedules.firstOrNull()
    }
}
