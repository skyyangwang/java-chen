drop procedure if exists  subscribe;  
CREATE PROCEDURE `subscribe`(IN transferid BIGINT, IN tendermoney DECIMAL(20,8), IN userid BIGINT, IN addip VARCHAR(20), IN tenderType VARCHAR(1), OUT msg VARCHAR(5))
    COMMENT '债权转让-手动认购'
SUBSCRIBE:BEGIN
	DECLARE v_borrow_id INT;
  DECLARE v_borrow_name varchar(100);
	DECLARE v_borrow_apr decimal(20,8) DEFAULT 0;
	DECLARE v_borrow_timelimit INT;
	DECLARE v_borrow_style INT;

	DECLARE v_capital decimal(20,8) DEFAULT 0;
	DECLARE v_account_real decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_account decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_account_yes decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_userid INT;

	DECLARE v_transfer_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_no_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_remaind decimal(20,8) DEFAULT 0;

	/* 账户表变量 */
	DECLARE v_account_total decimal(20,8) DEFAULT 0;
	DECLARE v_account_usemoney decimal(20,8) DEFAULT 0;
	DECLARE v_account_nousemoney decimal(20,8) DEFAULT 0;
	DECLARE v_account_collection decimal(20,8) DEFAULT 0;
	DECLARE v_first_borrow_use_money decimal(20,8) DEFAULT 0;

	DECLARE v_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_no_draw_money decimal(20,8) DEFAULT 0;

	DECLARE v_tender_id int;

	DECLARE v_repayment_capital decimal(20,8) DEFAULT 0;
	DECLARE v_repayment_interest decimal(20,8) DEFAULT 0;
	DECLARE v_repayment_account decimal(20,8) DEFAULT 0;
	DECLARE v_repayment_capitalsum decimal(20,8) DEFAULT 0;
	DECLARE v_repayment_interestsum  decimal(20,8) DEFAULT 0;
	DECLARE v_repayment_accountsum  decimal(20,8) DEFAULT 0;

	DECLARE v_no_account  decimal(20,8) DEFAULT 0;
	DECLARE v_no_interest  decimal(20,8) DEFAULT 0;

	DECLARE v_money decimal(20,8) default 0;
  DECLARE v_isvip bigint;
	DECLARE o_userLevel VARCHAR(10);
	DECLARE o_ratio VARCHAR(10);
	DECLARE EXIT HANDLER FOR SQLEXCEPTION SET msg = '0000';
	
	/**投标方式（0：手动投标，1：自动投标，2：优先投标）---这里的为0；*/

		SET v_money = tendermoney;


  /**如果金额非法,退出存储过程*/
	IF v_money <= 0 THEN 
			SET msg = '00002';
	  LEAVE SUBSCRIBE;
  END IF;
		
		/**锁定transfer**/
  	SELECT BORROW_ID,BORROW_NAME,TENDER_ID,CAPITAL,ACCOUNT,ACCOUNT_REAL,ACCOUNT_YES,BORROW_APR,BORROW_TIME_LIMIT,USER_ID,BORROW_STYLE INTO 
  	v_borrow_id,v_borrow_name,v_tender_id,v_capital,v_transfer_account,v_account_real,v_transfer_account_yes,v_borrow_apr,v_borrow_timelimit,v_transfer_userid,v_borrow_style
  	FROM rocky_b_transfer WHERE id = transferid FOR UPDATE;
		
		SET v_transfer_remaind = v_account_real - v_transfer_account_yes;
		-- 如果标剩余金额小于投标金额，退出存储过程
		IF v_transfer_remaind < v_money THEN
					SET msg = '00003';
				LEAVE SUBSCRIBE;
		END IF;

  	/**锁定account**/
  	SELECT TOTAL,USE_MONEY,NO_USE_MONEY,COLLECTION,FIRST_BORROW_USE_MONEY,DRAW_MONEY,NO_DRAW_MONEY INTO 
  	v_account_total,v_account_usemoney,v_account_nousemoney,v_account_collection,v_first_borrow_use_money,v_draw_money,v_no_draw_money
  	FROM rocky_account WHERE USER_ID = userid FOR UPDATE;

  	/**更新债权转让标已借到的金额和投标次数**/
  	UPDATE rocky_b_transfer SET ACCOUNT_YES = ACCOUNT_YES+v_money,TENDER_TIMES = TENDER_TIMES + 1 WHERE id = transferid;
    SET v_transfer_account_yes = v_transfer_account_yes+v_money;

	  /**投标方式（0：手动投标，1：自动投标，2：优先投标）*/
		/**认购金额先从账户受限金额扣除，不够从可提现金额扣除；---下面是中间计算*/
			IF v_no_draw_money > 0 THEN
					IF v_no_draw_money >= v_money THEN
							SET v_transfer_no_draw_money = v_money;
							SET v_transfer_draw_money = 0;
					ELSE
							SET v_transfer_no_draw_money = v_no_draw_money;
							SET v_transfer_draw_money = v_money - v_no_draw_money;
					END IF;
			ELSE
					SET v_transfer_no_draw_money = 0;
					SET v_transfer_draw_money = v_money;
			END IF;
			

		/*待收记录表查询-状态0-尚未支付的，总代收，总利息*/
		select sum(REPAY_ACCOUNT),sum(INTEREST) INTO
		v_no_account,v_no_interest
		from  rocky_b_collectionrecord  where TENDER_ID = v_tender_id and `STATUS`=0;

		/*认购情形-一标满*/  
		IF v_money = v_account_real THEN
			/*认购表-本金*/  
			SET v_repayment_capital = v_capital;
			/*认购表-利息*/
			SET v_repayment_interest = v_no_interest;
			/*认购表-代收*/
			SET v_repayment_account = v_no_account;

		/*认购情形-最后一标*/
		ELSEIF v_money = v_transfer_remaind  THEN

		/*认购表查询，本金、利息、代收*/
		select sum(REPAYMENT_CAPITAL),sum(REPAYMENT_INTEREST),sum(REPAYMENT_ACCOUNT) INTO
		v_repayment_capitalsum,v_repayment_interestsum,v_repayment_accountsum
  	FROM rocky_b_subscribe WHERE TRANSFER_ID = transferid;

			/*认购表-本金*/
			SET v_repayment_capital = v_capital-v_repayment_capitalsum;
			/*认购表-利息*/
			SET v_repayment_interest = v_no_interest-v_repayment_interestsum;
			/*认购表-代收*/
			SET v_repayment_account = v_no_account-v_repayment_accountsum;
	
		/*认购情形-普通*/	
		ELSE	
			/*认购表-本金*/
			SET v_repayment_capital = ROUND(v_capital*ROUND(v_money/v_account_real,8),2);
			/*认购表-利息*/
			SET v_repayment_interest = ROUND(v_no_interest*ROUND(v_money/v_account_real,8),2);
			/*认购表-代收*/
			SET v_repayment_account = ROUND(v_no_account*ROUND(v_money/v_account_real,8),2);
		END IF;
		
	
		/*是否是vip*/
    SET v_isvip = IFNULL( (SELECT PASSED  from rocky_vip_appro WHERE USER_ID = v_transfer_userid),0);
    IF v_isvip = -1 THEN
         SET v_isvip = 0;
    END IF;
		/**获得用户会员等级和比率*/
	  CALL getUserLevelRatio(v_transfer_userid,o_userLevel,o_ratio);
    
    	/**新增认购记录**/
  		INSERT INTO `rocky_b_subscribe` (`USER_ID`, `TRANSFER_ID`,BORROW_ID, `ACCOUNT`, `REPAYMENT_CAPITAL`, `REPAYMENT_INTEREST`, `REPAYMENT_ACCOUNT`, `DRAW_MONEY`, `NO_DRAW_MONEY`, `USER_LEVEL`, `RATIO`,`IS_VIP`, `STATUS`,  `ADD_TIME`,`ADD_IP`,`SUBSCRIBE_TYPE`) 
  		VALUES ( userid,transferid,v_borrow_id,v_money,v_repayment_capital,v_repayment_interest,v_repayment_account,0,0,o_userLevel,o_ratio,v_isvip,0,current_timestamp,addip,0);
			
			/** 更新账户资金 **/
			UPDATE rocky_account SET USE_MONEY = USE_MONEY - v_money, NO_USE_MONEY = NO_USE_MONEY + v_money, DRAW_MONEY = DRAW_MONEY - v_transfer_draw_money, NO_DRAW_MONEY = NO_DRAW_MONEY - v_transfer_no_draw_money,collection=collection+v_repayment_account WHERE USER_ID = userid;

			/**新增债权转让冻结log**/		
			INSERT INTO rocky_accountlog (USER_ID,TYPE,TOTAL,MONEY,USE_MONEY,NO_USE_MONEY,COLLECTION,TO_USER,REMARK,ADDIP,ADDTIME,`DRAW_MONEY`,`NO_DRAW_MONEY`,FIRST_BORROW_USE_MONEY,BORROW_ID, BORROW_NAME)
			VALUES (userid, 'transfer_cold',v_account_total,v_money,v_account_usemoney-v_money,v_account_nousemoney+v_money,v_repayment_account,v_transfer_userid,'按手动认购方式认购，资金冻结成功。',addip,current_date,v_draw_money - v_transfer_draw_money, v_no_draw_money - v_transfer_no_draw_money, v_first_borrow_use_money,v_borrow_id,v_borrow_name);

	
	/**满标-修改债权转让表状态*/
	SET v_transfer_remaind = v_account_real - v_transfer_account_yes;
   IF v_transfer_account_yes = v_transfer_account THEN
      UPDATE rocky_b_transfer SET `STATUS` = 3,SUCCESS_TIME = current_timestamp  WHERE ID = transferid;
    END IF;
  

	  /**操作成功*/
		SET msg = '0001'; 
END