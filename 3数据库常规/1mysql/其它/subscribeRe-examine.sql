drop procedure if exists  subscribeReexamine;  
CREATE PROCEDURE `subscribeReexamine`(IN transferid BIGINT,OUT msg VARCHAR(5))
    COMMENT '认购-满标后自动复审'
SUBSCRIBEREEXAMINE:BEGIN

	/* 定义认购表变量 */
  declare v_subscribe_id int(11);
  declare v_subscribe_user_id int(11);
  declare v_subscribe_transfer_id int(11);
  declare v_subscribe_borrow_id int(11);
  declare v_subscribe_account decimal(20,2);
  declare v_subscribe_repayment_capital decimal(20,2);
  declare v_subscribe_repayment_interest decimal(20,2);
  declare v_subscribe_repayment_account decimal(20,2);
  declare v_subscribe_draw_money decimal(20,2);
  declare v_subscribe_no_draw_money decimal(20,2);
  declare v_subscribe_user_level varchar(5);
  declare v_subscribe_ratio varchar(10);
  declare v_subscribe_is_vip tinyint(1);
  declare v_subscribe_status tinyint(1);
	declare v_subscribe_add_ip varchar(64);

	/* 债转转让表变量 */
	DECLARE v_account_real decimal(20,2);
	DECLARE v_tender_id int;
	DECLARE v_transfer_user_id int;
	DECLARE v_transfer_capital decimal(20,2);
	DECLARE v_transfer_account_real decimal(20,2);
	DECLARE v_transfer_manage_fee decimal(20,2);
	DECLARE v_borrow_id INT;
  DECLARE v_borrow_name varchar(100);
	DECLARE v_add_ip varchar(64);

	/* 账户表变量 */
	DECLARE v_account_total decimal(20,2);
	DECLARE v_account_usemoney decimal(20,2);
	DECLARE v_account_nousemoney decimal(20,2);
	DECLARE v_account_collection decimal(20,2);
	DECLARE v_account_draw_money decimal(20,2);
	DECLARE v_account_no_draw_money decimal(20,2);
	DECLARE v_first_borrow_use_money decimal(20,2);


	DECLARE v_new_tender_id int;
	DECLARE v_borrow_style int;
	DECLARE v_cnum int;
	DECLARE v_num int  default 0;  

	/* 待收总额 */
	DECLARE v_no_account decimal(20,2);

	/* 认购总额 */
	DECLARE v_buy_moneysum decimal(20,2);

  declare done  int default 0;
  /* 定义认购表查询游标 */
  declare c_subscribe cursor for 
    select
     ID, USER_ID, TRANSFER_ID, BORROW_ID, ACCOUNT,
     REPAYMENT_CAPITAL, REPAYMENT_INTEREST, REPAYMENT_ACCOUNT,DRAW_MONEY, NO_DRAW_MONEY, 
		 USER_LEVEL, RATIO, IS_VIP, `STATUS`,ADD_IP
    from rocky_b_subscribe where TRANSFER_ID = transferid for update;
	declare continue handler for not found set done = 1;  

	DECLARE EXIT HANDLER FOR SQLEXCEPTION SET msg = '0000';
	

  /**数据验证:购买金额总值=转让价格； 满标金额验证---通过/不通过;*/
	/**查询债权转让表**/
	SELECT ACCOUNT_REAL,TENDER_ID,USER_ID,CAPITAL,ACCOUNT_REAL,MANAGE_FEE,BORROW_ID,BORROW_NAME,ADD_IP INTO
  	v_account_real,v_tender_id,v_transfer_user_id,v_transfer_capital,v_transfer_account_real,v_transfer_manage_fee,v_borrow_id,v_borrow_name,v_add_ip
  	FROM rocky_b_transfer WHERE ID = transferid FOR UPDATE;
	/**查询认购记录表**/
	select sum(ACCOUNT) INTO
		v_buy_moneysum
  	FROM rocky_b_subscribe WHERE TRANSFER_ID = transferid;

	IF v_account_real != v_buy_moneysum THEN 
			SET msg = '00002';
	  LEAVE SUBSCRIBEREEXAMINE;
  END IF;

	/**锁定account**/
  	SELECT TOTAL,USE_MONEY,NO_USE_MONEY,COLLECTION,FIRST_BORROW_USE_MONEY,DRAW_MONEY,NO_DRAW_MONEY INTO 
  	v_account_total,v_account_usemoney,v_account_nousemoney,v_account_collection,v_first_borrow_use_money,v_account_draw_money,v_account_no_draw_money
  	FROM rocky_account WHERE USER_ID = transferid FOR UPDATE;
		
		
	/**修改表数据:债权转让表；认购表；投标记录表；代收记录表；*/
	UPDATE rocky_b_transfer SET `STATUS` = 4 WHERE ID = transferid;
    
  UPDATE rocky_b_subscribe SET `STATUS` = 3 WHERE TRANSFER_ID = transferid;

	UPDATE rocky_b_tenderrecord SET `STATUS` = -2 WHERE ID = v_tender_id;
	
	UPDATE rocky_b_collectionrecord SET `STATUS` = -1 WHERE TENDER_ID = v_tender_id and `STATUS`=0;

	
	/**添加投标记录*/

  open c_subscribe;
 
  repeat
    fetch c_subscribe into v_subscribe_id, v_subscribe_user_id, v_subscribe_transfer_id, v_subscribe_borrow_id, v_subscribe_account, v_subscribe_repayment_capital, v_subscribe_repayment_interest, v_subscribe_repayment_account, v_subscribe_draw_money, v_subscribe_no_draw_money, v_subscribe_user_level, v_subscribe_ratio, v_subscribe_is_vip, v_subscribe_status,v_subscribe_add_ip;

    if not done then
       insert into rocky_b_tenderrecord(USER_ID, BORROW_ID, STATUS, ACCOUNT, INTEREST, REPAYMENT_ACCOUNT, ADDTIME, ADDIP, TENDER_TYPE, USER_LEVEL, RATIO, DRAW_MONEY, NO_DRAW_MONEY, IS_VIP, PARENT_ID)
    values (v_subscribe_user_id, v_subscribe_borrow_id, 1, v_subscribe_repayment_capital, v_subscribe_repayment_interest, v_subscribe_repayment_account, unix_timestamp(), v_subscribe_add_ip, 0, v_subscribe_user_level, v_subscribe_ratio, v_subscribe_draw_money, v_subscribe_no_draw_money, v_subscribe_is_vip, v_tender_id);

		/* 获取新插入的tenderId */
    select last_insert_id() into v_new_tender_id;

		/* 从借款标获得还款方式*/  
		select STYLE INTO
		v_borrow_style
		from rocky_borrow where ID = v_subscribe_borrow_id;
		
		/* 插入待收记录(从转让债权的投标原始待收记录中获取)*/
		IF v_borrow_style in (3,4)  then
    /* 一条代收记录*/
    insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
    select 0, `ORDER`, v_new_tender_id, REPAY_TIME, v_subscribe_repayment_account, v_subscribe_repayment_interest, v_subscribe_repayment_capital, unix_timestamp(), v_subscribe_add_ip, v_subscribe_user_id, BORROW_ID from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0;

		ELSEIF v_borrow_style=1  then
		/* 等额本息，认购表中的代收金额/未还款的记录数=每条代收记录的预收金额*/
		select count(*) as cnum  INTO
		v_cnum
		from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0;

		insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
			select 0, `ORDER`, v_new_tender_id, REPAY_TIME, v_subscribe_repayment_account/v_cnum, v_subscribe_repayment_interest/v_cnum, v_subscribe_repayment_capital/v_cnum, unix_timestamp(), v_subscribe_add_ip, v_subscribe_user_id, BORROW_ID from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0;
		
		ELSE
		/* 按月付息到期还本；如果是已是最后一条记录的，则和“一条代收记录”的情形同*/
		SET v_num = v_cnum-1;
			IF v_num>0 THEN
			insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
				select 0, `ORDER`, v_new_tender_id, REPAY_TIME, v_subscribe_repayment_interest/v_cnum, v_subscribe_repayment_interest/v_cnum, 0, unix_timestamp(), v_subscribe_add_ip, v_subscribe_user_id, BORROW_ID from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0  order by REPAY_TIME  limit 0,v_num;
			insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
				select 0, `ORDER`, v_new_tender_id, REPAY_TIME, v_subscribe_repayment_interest/v_cnum+v_subscribe_repayment_capital, v_subscribe_repayment_interest/v_cnum, v_subscribe_repayment_capital, unix_timestamp(), v_subscribe_add_ip, v_subscribe_user_id, BORROW_ID from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0  order by REPAY_TIME DESC  limit 0,1;
			ELSE
			insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
				select 0, `ORDER`, v_new_tender_id, REPAY_TIME, v_subscribe_repayment_account, v_subscribe_repayment_interest, v_subscribe_repayment_capital, unix_timestamp(), v_subscribe_add_ip, v_subscribe_user_id, BORROW_ID from rocky_b_collectionrecord where TENDER_ID = v_tender_id and STATUS = 0  order by REPAY_TIME DESC  limit 0,1;
			END IF;
		END IF;

    end if;
  until done end repeat;
 
  close c_subscribe;


		/*待收记录表查询-状态0-尚未支付的，总待收*/
		select sum(REPAY_ACCOUNT) INTO
		v_no_account
		from  rocky_b_collectionrecord  where TENDER_ID = v_tender_id and `STATUS`=0;

		/**变更转让人账户信息*/
		UPDATE rocky_account SET USE_MONEY = USE_MONEY +v_transfer_account_real-v_transfer_manage_fee, NO_USE_MONEY = NO_USE_MONEY -v_transfer_capital, NO_DRAW_MONEY = NO_DRAW_MONEY +v_transfer_account_real-v_transfer_manage_fee,collection=collection-v_no_account,total=total+v_transfer_account_real-v_transfer_manage_fee-v_transfer_capital WHERE USER_ID = v_transfer_user_id;

		/**新增债权转让成功log**/		
		INSERT INTO rocky_accountlog (USER_ID,TYPE,TOTAL,MONEY,USE_MONEY,NO_USE_MONEY,COLLECTION,TO_USER,REMARK,ADDIP,ADDTIME,`DRAW_MONEY`,`NO_DRAW_MONEY`,FIRST_BORROW_USE_MONEY,BORROW_ID, BORROW_NAME)
			VALUES (v_transfer_user_id, 'transfer_success',v_account_total+v_transfer_account_real-v_transfer_manage_fee-v_transfer_capital,v_transfer_account_real-v_transfer_manage_fee,v_account_usemoney +v_transfer_account_real-v_transfer_manage_fee,v_account_nousemoney -v_transfer_capital,v_account_collection-v_no_account,v_transfer_userid,'债权转让成功，资金转入记录。',v_add_ip,current_date,v_account_draw_money, v_account_no_draw_money+v_transfer_account_real-v_transfer_manage_fee, v_first_borrow_use_money,v_borrow_id,v_borrow_name);



	  /**操作成功*/
		SET msg = '0001'; 
END