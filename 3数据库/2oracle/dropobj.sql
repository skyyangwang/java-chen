
set heading off;
set feedback off;
spool d:\dropobj.sql;
  prompt --Drop constraint
 select 'alter table '||table_name||' drop constraint '||constraint_name||' ;' from user_constraints where constraint_type='R';
 prompt --Drop tables
 select 'drop table '||table_name ||';' from user_tables; 
 
 prompt --Drop view
 select 'drop view ' ||view_name||';' from user_views;
 
 prompt --Drop sequence
 select 'drop sequence ' ||sequence_name||';' from user_sequences; 
 
 prompt --Drop function
 select 'drop function ' ||object_name||';'  from user_objects  where object_type='FUNCTION';

 prompt --Drop procedure
 select 'drop procedure '||object_name||';' from user_objects  where object_type='PROCEDURE';
 
 prompt --Drop package
 prompt --Drop package body
 select 'drop package '|| object_name||';' from user_objects  where object_type='PACKAGE';

 prompt --Drop database link
 select 'drop database link '|| object_name||';' from user_objects  where object_type='DATABASE LINK';
 
spool off;
set heading on;
set feedback on;

@@d:\dropobj.sql;
host del d:\dropobj.sql;