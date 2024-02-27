package com.springboot.compiler.Repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.springboot.compiler.Models.Status;
import com.springboot.compiler.Models.Submissions;
import com.springboot.compiler.Projections.SubmissionCountProjection;
import com.springboot.compiler.Projections.SubmissionDetailsProjection;

import jakarta.transaction.Transactional;

public interface SubmissionsRepository extends JpaRepository<Submissions, Integer> {

        // query to insert a recored into submission table
        @Modifying
        @Transactional
        @Query(value = "INSERT INTO submissions (language,run_time,status,submission_date,submission_id,username) VALUES(:language,:run_time,:status,:date,:id,:username)", nativeQuery = true)
        public void insertIntoSubmissions(@Param("language") String language, @Param("run_time") String runTime,
                        @Param("status") String status, @Param("date") Date date, @Param("id") String id,
                        @Param("username") String username);

        // query to select submission based on ID
        @Query("SELECT u.username AS username,s.submissionId AS submissionId, s.language AS language, s.status AS status,s.runTime AS runTime FROM Submissions s JOIN s.username u WHERE s.submissionId=:id")
        public SubmissionDetailsProjection getSubmissionDetails(@Param("id") String submissionId);

        // query to select the user submission
        @Query("SELECT s.submissionId AS submissionId,s.language AS language,s.status AS status FROM Submissions s JOIN s.username u WHERE u.username=:username")
        public Page<SubmissionDetailsProjection> getUserSubmissionDetails(@Param("username") String username,
                        Pageable page);

        // query to select the user submission based on language
        @Query("SELECT s.submissionId AS submissionId,s.language AS language,s.status AS status FROM Submissions s JOIN s.username u WHERE u.username=:username AND s.language=:language")
        public Page<SubmissionDetailsProjection> getUserSubmissionDetailsWithLanguage(
                        @Param("username") String username,
                        @Param("language") String language, Pageable page);

        // query to select the user submission based on status
        @Query("SELECT s.submissionId AS submissionId,s.language AS language,s.status AS status FROM Submissions s JOIN s.username u WHERE u.username=:username AND s.status=:status")
        public Page<SubmissionDetailsProjection> getUserSubmissionDetailsWithStatus(@Param("username") String username,
                        @Param("status") Status status, Pageable page);

        // query to select the user submission based on language and status
        @Query("SELECT s.submissionId AS submissionId,s.language AS language,s.status AS status FROM Submissions s JOIN s.username u WHERE u.username=:username AND s.language=:language AND s.status=:status")
        public Page<SubmissionDetailsProjection> getUserSubmissionDetailsWithStatusAndLanguage(
                        @Param("username") String username, @Param("language") String language,
                        @Param("status") Status status,
                        Pageable page);

        // procedure to delete the submission from Db
        @Procedure(value = "deleteSubmission")
        public Integer deleteSubmission(String submissionId, String username);

        // query to select the user submission in last hours
        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM Submissions s JOIN s.username u WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND u.username=:username")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHourUser(@Param("username") String username,
                        @Param("hours") int hours, Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM Submissions s JOIN s.username u WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND u.username=:username AND s.language=:language")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHourUserWithLanguage(
                        @Param("username") String username, @Param("hours") int hours,
                        @Param("language") String language,
                        Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM Submissions s JOIN s.username u WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND u.username=:username AND s.status=:status")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHourUserWithStatus(
                        @Param("username") String username,
                        @Param("hours") int hours, @Param("status") Status status, Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM Submissions s JOIN s.username u WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND u.username=:username AND s.language=:language AND s.status=:status")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHourUserWithStatusAndLanguage(
                        @Param("username") String username, @Param("hours") int hours,
                        @Param("language") String language,
                        @Param("status") Status status, Pageable page);

        // query to select the user submission in last hours
        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM  Submissions s JOIN s.username u  WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHours(@Param("hours") int hours, Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM  Submissions s JOIN s.username u  WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND s.language=:language")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHoursWithLanguage(@Param("hours") int hours,
                        @Param("language") String language, Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM  Submissions s JOIN s.username u  WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours AND s.status=:status")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHoursWithStatus(@Param("hours") int hours,
                        @Param("status") Status status, Pageable page);

        @Query("SELECT s.submissionId AS submissionId,s.language  AS language, u.username AS username,s.status AS status,s.submissionDate AS date FROM  Submissions s JOIN s.username u  WHERE TIMESTAMPDIFF(HOUR,s.submissionDate,CURRENT_TIMESTAMP())<=:hours  AND s.language=:language AND s.status=:status")
        public Page<SubmissionDetailsProjection> getSubmissionsInLastHoursWithStatusAndLanguage(
                        @Param("hours") int hours,
                        @Param("language") String language, @Param("status") Status status, Pageable page);

        @Query("SELECT s.language AS language, COUNT(s.submissionId) AS count, SUM(IF(s.status='SUCCESS',1,0)) AS successCount ,SUM(IF(DATEDIFF(CURDATE(),s.submissionDate)<=1,1,0)) AS pastCount FROM Submissions s JOIN s.username u WHERE u.username=:username GROUP BY s.language")
        public List<SubmissionCountProjection> getSubmissionCounts(@Param("username") String username);
}
