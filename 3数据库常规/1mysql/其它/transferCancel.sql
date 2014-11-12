drop procedure if exists  transferCancel;  
CREATE PROCEDURE `transferCancel`(IN transferid BIGINT, IN userid BIGINT, IN cancelip VARCHAR(20), IN cancelRemark VARCHAR(100), OUT msg VARCHAR(5))
    COMMENT '债权转让-撤销操作'
TRANSFERCANCEL:BEGIN

	/* 定义认购表变量 */
  declare v_subscribe_user_id int(11);
  declare v_subscribe_account decimal(20,2);
  declare v_subscribe_repayment_account decimal(20,2);

	/* 债权转让表变量 */
	DECLARE v_borrow_id INT;
  DECLARE v_borrow_name varchar(100);
	
	/* 账户表变量 */
	DECLARE v_account_total decimal(20,2);
	DECLARE v_account_usemoney decimal(20,2);
	DECLARE v_account_nousemoney decimal(20,2);
	DECLARE v_account_collection decimal(20,2);
	DECLARE v_account_draw_money decimal(20,2);
	DECLARE v_account_no_draw_money decimal(20,2);
	DECLARE v_first_borrow_use_money decimal(20,2);

	declare done  int default 0;
  /* 定义认购表查询游标 */
  declare c_subscribe cursor for 
    select
     USER_ID, ACCOUNT, REPAYMENT_ACCOUNT
    from rocky_b_subscribe where TRANSFER_ID = transferid for update;
	declare continue handler for not found set done = 1;  

	DECLARE EXIT HANDLER FOR SQLEXCEPTION SET msg = '0000';
	

	/**更新债权转让表，状态：撤销**/
		UPDATE rocky_b_transfer SET `STATUS` = 6,CANCEL_USER = userid,CANCEL_TIME = current_timestamp,CANCEL_IP = cancelip,CANCEL_REMARK = cancelRemark  WHERE ID = transferid;
    
	/**更新认购表，TRANSFER_ID：传入的参数transferid**/
		UPDATE rocky_b_subscribe SET `STATUS` = 2  WHERE TRANSFER_ID = transferid;
    
	/**查询债权转让表中的两个变量，后面使用**/
		SELECT BORROW_ID,BORROW_NAME INTO 
  	v_borrow_id,v_borrow_name
  	FROM rocky_b_transfer WHERE ID = transferid;


	/**账户表，账户日志表操作；---循环操作，游标使用**/
	open c_subscribe;
 
		repeat
			fetch c_subscribe into v_subscribe_user_id, v_subscribe_account, v_subscribe_repayment_account;

		if not done then
	/**锁定account**/
  	SELECT TOTAL,USE_MONEY,NO_USE_MONEY,COLLECTION,FIRST_BORROW_USE_MONEY,DRAW_MONEY,NO_DRAW_MONEY INTO 
  	v_account_total,v_account_usemoney,v_account_nousemoney,v_account_collection,v_first_borrow_use_money,v_account_draw_money,v_account_no_draw_money
  	FROM rocky_account WHERE USER_ID = v_subscribe_user_id FOR UPDATE;

	/** 更新账户资金 **/
		UPDATE rocky_account SET USE_MONEY = USE_MONEY + v_subscribe_account, NO_USE_MONEY = NO_USE_MONEY - v_subscribe_account, NO_DRAW_MONEY = NO_DRAW_MONEY + v_subscribe_account,collection=collection-v_subscribe_repayment_account WHERE USER_ID = v_subscribe_user_id;

		/**新增债权转让撤销log,交易对方id为空-0**/		
		INSERT INTO rocky_accountlog (USER_ID,TYPE,TOTAL,MONEY,USE_MONEY,NO_USE_MONEY,COLLECTION,TO_USER,REMARK,ADDIP,ADDTIME,`DRAW_MONEY`,`NO_DRAW_MONEY`,FIRST_BORROW_USE_MONEY,BORROW_ID, BORROW_NAME)
			VALUES (v_subscribe_user_id, 'transfer_cancel',v_account_total,v_subscribe_account,v_account_usemoney + v_subscribe_account,v_account_nousemoney - v_subscribe_account,v_account_collection-v_subscribe_repayment_account,0,'债权转让撤销，资金回滚',cancelip,UNIX_TIMESTAMP(),v_account_draw_money, v_account_no_draw_money + v_subscribe_account, v_first_borrow_use_money,v_borrow_id,v_borrow_name);


    end if;
  until done end repeat;
 
  close c_subscribe;
  

	  /**操作成功*/
		SET msg = '0001'; 
END