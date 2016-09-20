
SELECT	 				USER_INFO.AccountId,
						USER_INFO.UserName,
                        USER_INFO.Email,
                        USER_INFO.FieldId,
                        USER_INFO.Field,
                        job.JobId,
						job_recommended.Rating,
                        category.CategoryId,
                        category.Description,
                        COUNT(*) AS USER
FROM
(
		SELECT 				account.AccountId,
							UserName,
							Email,
							care.CategoryId FieldId,
							Description AS Field
		FROM 				account, 
							care, 
							category
			
		WHERE 				(care.CategoryId = 8)
		AND					account.AccountId = care.AccountId
		AND					care.CategoryId = category.CategoryId
) AS USER_INFO, job_recommended, job, category

WHERE	 	USER_INFO.AccountId = job_recommended.AccountId
AND			job.JobId  =  job_recommended.JobId
AND 		job_recommended.Rating > 0
AND			category.CategoryId = job.CategoryId



GROUP BY 				job_recommended.Rating,
                        category.CategoryId,
                        category.Description   
LIMIT 0,100000000		

/*--------------------------------------------------------------------*/




SELECT	 				
                     
						job_recommended.Rating,                        
                        category.Description,
                        category.CategoryId,
                        COUNT(*) AS TOTAL_RATE
FROM
(
		SELECT 				account.AccountId,
							UserName,
							Email,
							care.CategoryId FieldId,
							Description AS Field
		FROM 				account, 
							care, 
							category
			
		WHERE				(care.CategoryId = 1) 				
		AND					account.AccountId = care.AccountId
		AND					care.CategoryId = category.CategoryId
) AS USER_INFO, job_recommended, job, category

WHERE	 	USER_INFO.AccountId = job_recommended.AccountId
AND			job.JobId  =  job_recommended.JobId
AND 		job_recommended.Rating > 0
AND			category.CategoryId = job.CategoryId



GROUP BY 				job_recommended.Rating,
                        category.CategoryId,
                        category.Description
LIMIT 0,100000000		



