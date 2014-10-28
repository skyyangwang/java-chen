drop procedure if exists  subscribe;  
CREATE PROCEDURE `subscribe`(IN transferid BIGINT, IN tendermoney DECIMAL(20,8), IN userid BIGINT, IN addip VARCHAR(20), IN tenderType VARCHAR(1), OUT msg VARCHAR(5))
    COMMENT '手动认购'
SUBSCRIBE:BEGIN
	DECLARE v_borrow_id INT;
  DECLARE v_borrow_name varchar(100);
	DECLARE v_borrow_apr decimal(20,8) DEFAULT 0;
	DECLARE v_borrow_timelimit INT;
	DECLARE v_borrow_style INT;

	DECLARE v_transfer_account decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_account_yes decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_userid INT;
	DECLARE v_btransfer_endtime varchar(20);

	DECLARE v_transfer_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_no_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_transfer_remaind decimal(20,8) DEFAULT 0;

	DECLARE v_account_total decimal(20,8) DEFAULT 0;
	DECLARE v_account_usemoney decimal(20,8) DEFAULT 0;
	DECLARE v_account_nousemoney decimal(20,8) DEFAULT 0;
	DECLARE v_account_collection decimal(20,8) DEFAULT 0;
	DECLARE v_first_borrow_use_money decimal(20,8) DEFAULT 0;

	DECLARE v_draw_money decimal(20,8) DEFAULT 0;
	DECLARE v_no_draw_money decimal(20,8) DEFAULT 0;

	DECLARE v_tender_id int;
	
	DECLARE v_interest decimal(20,8);
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
  	SELECT BORROW_ID,BORROW_NAME,ACCOUNT,ACCOUNT_YES,BORROW_APR,BORROW_TIME_LIMIT,USER_ID,BORROW_STYLE INTO 
  	v_borrow_id,v_borrow_name,v_transfer_account,v_transfer_account_yes,v_borrow_apr,v_borrow_timelimit,v_transfer_userid,v_borrow_style
  	FROM rocky_b_transfer WHERE id = transferid FOR UPDATE;
		
		SET v_transfer_remaind = v_transfer_account - v_transfer_account_yes;
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
			/** 更新账户资金 **/
			UPDATE rocky_account SET USE_MONEY = USE_MONEY - v_money, NO_USE_MONEY = NO_USE_MONEY + v_money, DRAW_MONEY = DRAW_MONEY - v_transfer_draw_money, NO_DRAW_MONEY = NO_DRAW_MONEY - v_transfer_no_draw_money WHERE USER_ID = userid;

    IF v_borrow_style = 3 THEN
      /**按月到期还本付息**/
      SET v_interest = ROUND(v_money*v_borrow_apr/100/12*v_borrow_timelimit,2);
    ELSEIF v_borrow_style = 4 THEN
      /**按天还款**/
      SET v_interest = ROUND(v_money*v_borrow_apr/100/360*v_borrow_timelimit,2);
    ELSEIF v_borrow_style = 1 THEN
      /**等额本息**/
      SET v_interest = ROUND((((v_money*(v_borrow_apr/100/12)*POW(v_borrow_apr/100/12+1,v_borrow_timelimit)/(POW(v_borrow_apr/100/12+1,v_borrow_timelimit)-1)))*v_borrow_timelimit-tendermoney),2);      
    ELSEIF v_borrow_style = 2 THEN
      /**按月付息到期还本**/
      SET v_interest = ROUND(v_money*v_borrow_apr/100/12*v_borrow_timelimit,2);
    END IF;
	
		/*是否是vip*/
    SET v_isvip = IFNULL( (SELECT PASSED  from rocky_vip_appro WHERE USER_ID = userid),0);
    IF v_isvip = -1 THEN
         SET v_isvip = 0;
    END IF;
		/**获得用户会员等级和比率*/
	  CALL getUserLevelRatio(userid,o_userLevel,o_ratio);
    
    	/**新增认购记录**/
  		INSERT INTO `rocky_b_subscribe` (`USER_ID`, `TRANSFER_ID`,BORROW_ID, `ACCOUNT`, `REPAYMENT_CAPITAL`, `REPAYMENT_INTEREST`, `REPAYMENT_ACCOUNT`, `DRAW_MONEY`, `NO_DRAW_MONEY`, `USER_LEVEL`, `RATIO`,`IS_VIP`, `STATUS`,  `ADD_TIME`,`ADD_IP`,`SUBSCRIBE_TYPE`) 
  		VALUES ( userid,transferid,v_borrow_id,v_money,v_money,v_interest,v_interest+v_money,0,0,o_userLevel,o_ratio,v_isvip,0,current_timestamp,addip,0);
			/**新增债权转让冻结log**/		
			INSERT INTO rocky_accountlog (USER_ID,TYPE,TOTAL,MONEY,USE_MONEY,NO_USE_MONEY,COLLECTION,TO_USER,REMARK,ADDIP,ADDTIME,`DRAW_MONEY`,`NO_DRAW_MONEY`,FIRST_BORROW_USE_MONEY,BORROW_ID, BORROW_NAME)
			VALUES (userid, 'transfer_cold',v_account_total,v_money,v_account_usemoney-v_money,v_account_nousemoney+v_money,v_account_collection,v_transfer_userid,'按手动投标方式投标，资金冻结成功。',addip,current_date,v_draw_money - v_transfer_draw_money, v_no_draw_money - v_transfer_no_draw_money, v_first_borrow_use_money,v_borrow_id,v_borrow_name);

	
	SET v_transfer_remaind = v_transfer_account - v_transfer_account_yes;
   IF v_transfer_account_yes = v_transfer_account THEN
			IF v_borrow_style = 4 THEN
				SET v_btransfer_endtime =  UNIX_TIMESTAMP(DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL v_borrow_timelimit DAY));
			ELSE
				SET v_btransfer_endtime = UNIX_TIMESTAMP(DATE_ADD(CURRENT_TIMESTAMP(),INTERVAL v_borrow_timelimit MONTH));
			END IF;
      UPDATE rocky_b_transfer SET `STATUS` = 3,SUCCESS_TIME = current_timestamp,END_TIME = v_btransfer_endtime  WHERE ID = transferid;
    END IF;
  

	  /**操作成功*/
		SET msg = '0001'; 
END