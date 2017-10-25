SELECT JP.STRING_VAL as NUM_PROCESSO, JE.STATUS
  FROM %PREFIX%JOB_EXECUTION JE inner join %PREFIX%JOB_EXECUTION_PARAMS JP 
       on JE.JOB_EXECUTION_ID=JP.JOB_EXECUTION_ID
 WHERE JP.KEY_NAME='processo' 
   AND JE.JOB_EXECUTION_ID =
       (SELECT MAX(JOB_EXECUTION_ID) 
          FROM %PREFIX%JOB_EXECUTION JE inner join %PREFIX%JOB_INSTANCE JI 
               on JE.JOB_INSTANCE_ID=JI.JOB_INSTANCE_ID 
         WHERE JI.JOB_INSTANCE_ID = JE.JOB_INSTANCE_ID
	       AND JI.JOB_NAME=?)
