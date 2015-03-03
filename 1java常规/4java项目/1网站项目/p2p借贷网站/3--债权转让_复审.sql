drop procedure if exists `transfer_recheck`;
create procedure `transfer_recheck`(in v_transfer_id bigint, in v_add_ip varchar(20), in v_check_userid bigint, in v_check_remark varchar(50), in v_platform varchar(30), out msg varchar(5))
    comment '债权转让_复审'
transfer_recheck:begin

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
  declare v_subscribe_add_time datetime;
  declare v_subscribe_rn tinyint(3) unsigned;
  declare v_subscribe_account_sum decimal(20,2);
  declare v_subscribe_count tinyint(3) unsigned;

  /* 认购转待收记录 */
  declare v_transfer_collection_order_account decimal(20,2) default 0;
  declare v_transfer_collection_order_capital decimal(20,2) default 0;
  declare v_transfer_collection_order_interest decimal(20,2) default 0;
  declare v_transfer_collection_order_repaytime varchar(13);
  declare v_subscribe_collection_order_account decimal(20,2) default 0;
  declare v_subscribe_collection_order_capital decimal(20,2) default 0;
  declare v_subscribe_collection_order_interest decimal(20,2) default 0;
  declare v_subscribe_collection_order_account_tmp decimal(20,2) default 0;
  declare v_subscribe_collection_order_capital_tmp decimal(20,2) default 0;
  declare v_subscribe_collection_order_interest_tmp decimal(20,2) default 0;
  declare v_transfer_begin_order_tmp tinyint(3) unsigned;

  /* 借款标信息 */
  declare v_borrow_id int(11);
  declare v_borrow_name varchar(100);
  declare v_borrow_apr decimal(5,2);
  declare v_borrow_style tinyint(1);
  declare v_borrow_type tinyint(1);
  declare v_borrow_order tinyint(3);
  declare v_borrow_time_limit tinyint(3);

  /* 原始投标信息 */
  declare v_orig_tender_id int(11);

  /* 转让方用户信息 */
  declare v_transfer_user_id int(11);
  declare v_transfer_account_real decimal(20,2) default 0;
  declare v_transfer_account_yes decimal(20,2) default 0;
  declare v_transfer_manage_fee decimal(20,2) default 0;
  declare v_transfer_begin_order tinyint(3);
  declare v_transfer_status tinyint(1);
  declare v_transfer_interest decimal(20,2) default 0;

  /* 认购者账户信息 */
  declare v_account_total_subscribe decimal(20,8) default 0;
  declare v_account_usemoney_subscribe decimal(20,8) default 0;
  declare v_account_nousemoney_subscribe decimal(20,8) default 0;
  declare v_account_collection_subscribe decimal(20,8) default 0;
  declare v_account_draw_money_subscribe decimal(20,8) default 0;
  declare v_account_no_draw_money_subscribe decimal(20,8) default 0;
  declare v_account_first_borrow_use_money_subscribe decimal(20,8) default 0;

  /* 认购者本次投标ID */
  declare v_new_tender_id int(11);
  /* 认购者本次待收总额 */
  declare v_collection_repay_account_subscribe decimal(20,8) default 0;

  /* 转让者账户信息 */
  declare v_account_total_transfer decimal(20,8) default 0;
  declare v_account_usemoney_transfer decimal(20,8) default 0;
  declare v_account_nousemoney_transfer decimal(20,8) default 0;
  declare v_account_collection_transfer decimal(20,8) default 0;
  declare v_account_draw_money_transfer decimal(20,8) default 0;
  declare v_account_no_draw_money_transfer decimal(20,8) default 0;
  declare v_account_first_borrow_use_money_transfer decimal(20,8) default 0;
  /* 转让者本次待收总额 */
  declare v_collection_repay_account_transfer decimal(20,8) default 0;

  declare v_continue int default 0;
  /* 定义认购表查询游标 */
  declare c_subscribe cursor for 
    select
     ID, USER_ID, TRANSFER_ID, BORROW_ID, ACCOUNT,
     REPAYMENT_CAPITAL, REPAYMENT_INTEREST, REPAYMENT_ACCOUNT,
     DRAW_MONEY, NO_DRAW_MONEY, USER_LEVEL, RATIO, IS_VIP, `STATUS`, ADD_TIME,
     @rn := @rn + 1
    from rocky_b_subscribe,(select @rn := 0) x
    where TRANSFER_ID = v_transfer_id and `STATUS` = 1
    order by ID for update;
  declare continue handler for not found set v_continue = 1;

  /* 异常则退出并返回msg=00000 */
  declare exit handler for sqlexception set msg = '00000';

  /* 获取原始投标信息 */
  select BORROW_ID, BORROW_NAME, BORROW_APR, BORROW_STYLE, BORROW_TYPE, BORROW_ORDER, BORROW_TIME_LIMIT, TENDER_ID, USER_ID, ACCOUNT_REAL, ACCOUNT_YES, MANAGE_FEE, TRANSFER_BEGIN_ORDER, `STATUS`, INTEREST
  into v_borrow_id, v_borrow_name, v_borrow_apr, v_borrow_style, v_borrow_type, v_borrow_order, v_borrow_time_limit, v_orig_tender_id, v_transfer_user_id, v_transfer_account_real, v_transfer_account_yes, v_transfer_manage_fee, v_transfer_begin_order, v_transfer_status, v_transfer_interest
  from rocky_b_transfer where ID = v_transfer_id;

  /* 判断实际投标金额是否等于转让价格 */
  if v_transfer_status != 3 or v_transfer_account_real != v_transfer_account_yes then
    set msg = '00000';
    leave transfer_recheck;
  end if;

  select sum(ACCOUNT), count(*) into v_subscribe_account_sum, v_subscribe_count from rocky_b_subscribe where TRANSFER_ID = v_transfer_id and `STATUS` = 1;
  /* 判断已投标总额是否等于转让价格 */
  if v_subscribe_account_sum is null or v_subscribe_account_sum != v_transfer_account_real then
    set msg = '00000';
    leave transfer_recheck;
  end if;

  /* 认购者处理 */
  open c_subscribe;
  
  /* 更新转让状态 */
  update rocky_b_transfer set `STATUS` = 4 where ID = v_transfer_id;
  /* 更新转让审核状态 */
  update rocky_b_transfer_approved set `STATUS` = 5, VERIFY_USER2 = v_check_userid, VERIFY_TIME2 = now(), VERIFY_IP2 = v_add_ip, VERIFY_MAC2 = null, VERIFY_REMARK2 = v_check_remark where TRANSFER_ID = v_transfer_id;
  /* 更新认购状态 */
  update rocky_b_subscribe set `STATUS` = 3 where TRANSFER_ID = v_transfer_id and `STATUS` = 1;
  
  repeat
    fetch c_subscribe into v_subscribe_id, v_subscribe_user_id, v_subscribe_transfer_id, v_subscribe_borrow_id, v_subscribe_account, v_subscribe_repayment_capital, v_subscribe_repayment_interest, v_subscribe_repayment_account, v_subscribe_draw_money, v_subscribe_no_draw_money, v_subscribe_user_level, v_subscribe_ratio, v_subscribe_is_vip, v_subscribe_status, v_subscribe_add_time, v_subscribe_rn;
    if v_continue = 0 then

      /* 锁定投资用户 */
      select TOTAL, USE_MONEY, NO_USE_MONEY, COLLECTION, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY
      into v_account_total_subscribe, v_account_usemoney_subscribe, v_account_nousemoney_subscribe, v_account_collection_subscribe, v_account_draw_money_subscribe, v_account_no_draw_money_subscribe, v_account_first_borrow_use_money_subscribe
      from rocky_account where USER_ID = v_subscribe_user_id for update;

      /* 插入投标记录 */
      insert into rocky_b_tenderrecord(USER_ID, BORROW_ID, STATUS, ACCOUNT, INTEREST, REPAYMENT_ACCOUNT, ADDTIME, ADDIP, TENDER_TYPE, USER_LEVEL, RATIO, DRAW_MONEY, NO_DRAW_MONEY, IS_VIP, PARENT_ID, PLATFORM)
      values (v_subscribe_user_id, v_subscribe_borrow_id, 1, v_subscribe_repayment_capital, v_subscribe_repayment_interest, v_subscribe_repayment_account, unix_timestamp(v_subscribe_add_time), v_add_ip, 0, v_subscribe_user_level, v_subscribe_ratio, v_subscribe_draw_money, v_subscribe_no_draw_money, v_subscribe_is_vip, v_orig_tender_id, v_platform);

      /* 获取新插入的tenderId */
      select last_insert_id() into v_new_tender_id;

      /* 新生成的投标记录ID更新到认购表中 */
      update rocky_b_subscribe set TENDER_ID = v_new_tender_id where ID = v_subscribe_id;

      /* 每期循环时初始化 */
      set v_subscribe_collection_order_account_tmp = 0;
      set v_subscribe_collection_order_capital_tmp = 0;
      set v_subscribe_collection_order_interest_tmp = 0;
      set v_transfer_begin_order_tmp = v_transfer_begin_order;

      /* 生成待收 */
      while v_transfer_begin_order_tmp <= v_borrow_order do

        select REPAY_ACCOUNT, CAPITAL, INTEREST, REPAY_TIME
        into v_transfer_collection_order_account, v_transfer_collection_order_capital, v_transfer_collection_order_interest, v_transfer_collection_order_repaytime
        from rocky_b_collectionrecord where TENDER_ID = v_orig_tender_id and `ORDER` = v_transfer_begin_order_tmp and `STATUS` = 0;

        /* 多人认购的情况 */
        if v_subscribe_count > 1 then
          /* 最后一个认购人(n人),每期金额-前几个认购人的金额累加 */
          if v_subscribe_rn = v_subscribe_count then
            select
              v_transfer_collection_order_account - sum(c.REPAY_ACCOUNT),
              v_transfer_collection_order_capital - sum(c.CAPITAL),
              v_transfer_collection_order_interest - sum(c.INTEREST)
            into
              v_subscribe_collection_order_account,
              v_subscribe_collection_order_capital,
              v_subscribe_collection_order_interest
            from rocky_b_collectionrecord c, rocky_b_tenderrecord t
            where c.TENDER_ID = t.ID and t.PARENT_ID = v_orig_tender_id and `ORDER` = v_transfer_begin_order_tmp;
          end if;
        end if;

        /* 前几个认购人(n-1人) */
        if v_subscribe_count = 1 or v_subscribe_rn < v_subscribe_count then
          /* 最后一期(n期)金额,总金额-前几期累加金额 */
          if v_transfer_begin_order_tmp = v_borrow_order then
            set v_subscribe_collection_order_account = v_subscribe_repayment_account - v_subscribe_collection_order_account_tmp;
            set v_subscribe_collection_order_capital = v_subscribe_repayment_capital - v_subscribe_collection_order_capital_tmp;
            set v_subscribe_collection_order_interest = v_subscribe_collection_order_account - v_subscribe_collection_order_capital;

          /* 前几期(n-1期),按每期比例计算 */
          else
            set v_subscribe_collection_order_account = round(v_transfer_collection_order_account * truncate(v_subscribe_account / v_subscribe_account_sum, 8), 2);
            set v_subscribe_collection_order_capital = round(v_transfer_collection_order_capital * truncate(v_subscribe_account / v_subscribe_account_sum, 8), 2);
            set v_subscribe_collection_order_interest = v_subscribe_collection_order_account - v_subscribe_collection_order_capital;
          end if;

          set v_subscribe_collection_order_account_tmp = v_subscribe_collection_order_account_tmp + v_subscribe_collection_order_account;
          set v_subscribe_collection_order_capital_tmp = v_subscribe_collection_order_capital_tmp + v_subscribe_collection_order_capital;
          set v_subscribe_collection_order_interest_tmp = v_subscribe_collection_order_interest_tmp + v_subscribe_collection_order_interest;
        end if;

        /* 插入待收记录(从转让债权原始待收记录中获取) */
        insert into rocky_b_collectionrecord(`STATUS`, `ORDER`, TENDER_ID, REPAY_TIME, REPAY_ACCOUNT, INTEREST, CAPITAL, ADDTIME, ADDIP, USER_ID, BORROW_ID)
        values (0, v_transfer_begin_order_tmp, v_new_tender_id, v_transfer_collection_order_repaytime, v_subscribe_collection_order_account, v_subscribe_collection_order_interest, v_subscribe_collection_order_capital, unix_timestamp(), v_add_ip, v_subscribe_user_id, v_borrow_id);

        set v_transfer_begin_order_tmp = v_transfer_begin_order_tmp + 1;
      end while;

      /* 扣除账户冻结金额(解冻: 总额减少,不可用减少) */
      update rocky_account set TOTAL = TOTAL - v_subscribe_account, NO_USE_MONEY = NO_USE_MONEY - v_subscribe_account where USER_ID = v_subscribe_user_id;
      /* 总额减少(解冻: 总额减少,不可用减少) */
      set v_account_total_subscribe = v_account_total_subscribe - v_subscribe_account;
      /* 不可用总额减少(解冻: 总额减少,不可用减少) */
      set v_account_nousemoney_subscribe = v_account_nousemoney_subscribe - v_subscribe_account;
      /* 扣除账户冻结金额LOG(解冻: 总额减少,不可用减少) */
      insert into rocky_accountlog(USER_ID, TYPE, TOTAL, MONEY, USE_MONEY, NO_USE_MONEY, COLLECTION, TO_USER, REMARK, ADDTIME, ADDIP, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY, BORROW_ID, BORROW_NAME)
      values (v_subscribe_user_id, 'tender_transfer_success', v_account_total_subscribe, v_subscribe_account, v_account_usemoney_subscribe, v_account_nousemoney_subscribe, v_account_collection_subscribe, v_subscribe_user_id, '复审通过，扣除账户冻结金额。', unix_timestamp(), v_add_ip, v_account_draw_money_subscribe, v_account_no_draw_money_subscribe, v_account_first_borrow_use_money_subscribe, v_subscribe_borrow_id, v_borrow_name);

      /* 本次待收总额 */
      select sum(REPAY_ACCOUNT) into v_collection_repay_account_subscribe from rocky_b_collectionrecord where TENDER_ID = v_new_tender_id;
      /* 资金总额,待收总额增加(待收: 总额增加,代收增加) */
      set v_account_total_subscribe = v_account_total_subscribe + v_collection_repay_account_subscribe;
      set v_account_collection_subscribe = v_account_collection_subscribe + v_collection_repay_account_subscribe;
      /* 待收金额增加(待收: 总额增加,代收增加) */
      update rocky_account set TOTAL = TOTAL + v_collection_repay_account_subscribe, COLLECTION = COLLECTION + v_collection_repay_account_subscribe where USER_ID = v_subscribe_user_id;
      /* 待收金额增加LOG(待收: 总额增加,代收增加) */
      insert into rocky_accountlog(USER_ID, TYPE, TOTAL, MONEY, USE_MONEY, NO_USE_MONEY, COLLECTION, TO_USER, REMARK, ADDTIME, ADDIP, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY, BORROW_ID, BORROW_NAME)
      values (v_subscribe_user_id, 'collection_added', v_account_total_subscribe, v_collection_repay_account_subscribe, v_account_usemoney_subscribe, v_account_nousemoney_subscribe, v_account_collection_subscribe, v_subscribe_user_id, '复审通过，待收金额增加。', unix_timestamp(), v_add_ip, v_account_draw_money_subscribe, v_account_no_draw_money_subscribe, v_account_first_borrow_use_money_subscribe, v_subscribe_borrow_id, v_borrow_name);

      /* 仅当认购金额大于认购本金时,净值额度重计算 */
      if v_subscribe_account > v_subscribe_repayment_capital then
        /* 债权转让满标复审通过之后，可提金额大于净值额度，可提金额转入受限金额 */
        /* 参数依次为：用户id、借款标id,借标标标题,记录ip,净值额度表类型,资金日志表类型，资金日志表备注 */
        call deal_drawmoney_to_nodraw(v_subscribe_user_id, v_borrow_id, v_borrow_name, v_add_ip, 18, 'net_draw_to_nodraw_transfer_recheck', '债权转让复审通过之后，可提金额大于净值额度，可提金额转入受限金额');
      end if;
    end if;
  until v_continue end repeat;
  close c_subscribe;

  /* 锁定转让用户 */
  select TOTAL, USE_MONEY, NO_USE_MONEY, COLLECTION, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY
  into v_account_total_transfer, v_account_usemoney_transfer, v_account_nousemoney_transfer, v_account_collection_transfer, v_account_draw_money_transfer, v_account_no_draw_money_transfer, v_account_first_borrow_use_money_transfer
  from rocky_account where USER_ID = v_transfer_user_id for update;

  /* 更新装让方待收债权转让应得利息 */
--  update rocky_b_collectionrecord set TRANSFER_INTEREST = if(`ORDER` = v_transfer_begin_order, v_transfer_interest, 0), TRANSFER_TIME = now() where TENDER_ID = v_orig_tender_id and `STATUS` = 0 and `ORDER` >= v_transfer_begin_order;
  /* 更新转让方投标记录状态 */
  update rocky_b_tenderrecord set `STATUS` = -2 where ID = v_orig_tender_id;
  /* 更新转让方待收记录状态 */
  update rocky_b_collectionrecord set `STATUS` = -1 where TENDER_ID = v_orig_tender_id and `STATUS` = 0 and `ORDER` >= v_transfer_begin_order;

  /* 更新转让方资金(回款: 总额增加,可用增加,可提增加) */
  set v_account_total_transfer = v_account_total_transfer + v_transfer_account_real;
  set v_account_usemoney_transfer = v_account_usemoney_transfer + v_transfer_account_real;
  set v_account_draw_money_transfer = v_account_draw_money_transfer + v_transfer_account_real;
  /* 更新转让方资金(回款: 总额增加,可用增加,可提增加) */
  update rocky_account set TOTAL = TOTAL + v_transfer_account_real, USE_MONEY = USE_MONEY + v_transfer_account_real, DRAW_MONEY = DRAW_MONEY + v_transfer_account_real where USER_ID = v_transfer_user_id;
  /* 更新转让方资金LOG(回款: 总额增加,可用增加,可提增加) */
  insert into rocky_accountlog(USER_ID, TYPE, TOTAL, MONEY, USE_MONEY, NO_USE_MONEY, COLLECTION, TO_USER, REMARK, ADDTIME, ADDIP, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY, BORROW_ID, BORROW_NAME)
  values (v_transfer_user_id, 'transfer_success', v_account_total_transfer, v_transfer_account_real, v_account_usemoney_transfer, v_account_nousemoney_transfer, v_account_collection_transfer, v_transfer_user_id, '债权转让复审通过，转让回款成功。', unix_timestamp(), v_add_ip, v_account_draw_money_transfer, v_account_no_draw_money_transfer, v_account_first_borrow_use_money_transfer, v_borrow_id, v_borrow_name);


  /* 转让者本次待收总额 */
  select sum(REPAY_ACCOUNT) into v_collection_repay_account_transfer from rocky_b_collectionrecord where TENDER_ID = v_orig_tender_id and `STATUS` = -1;
  /* 更新转让方资金(待收: 总额减少,待收减少) */
  set v_account_total_transfer = v_account_total_transfer - v_collection_repay_account_transfer;
  set v_account_collection_transfer = v_account_collection_transfer - v_collection_repay_account_transfer;
  /* 更新转让方资金(待收: 总额减少,待收减少) */
  update rocky_account set TOTAL = TOTAL - v_collection_repay_account_transfer, COLLECTION = COLLECTION - v_collection_repay_account_transfer where USER_ID = v_transfer_user_id;
  /* 更新转让方资金LOG(待收: 总额减少,待收减少) */
  insert into rocky_accountlog(USER_ID, TYPE, TOTAL, MONEY, USE_MONEY, NO_USE_MONEY, COLLECTION, TO_USER, REMARK, ADDTIME, ADDIP, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY, BORROW_ID, BORROW_NAME)
  values (v_transfer_user_id, 'transfer_collection_account', v_account_total_transfer, v_collection_repay_account_transfer, v_account_usemoney_transfer, v_account_nousemoney_transfer, v_account_collection_transfer, v_transfer_user_id, '债权转让复审通过，扣除转让待收金额。', unix_timestamp(), v_add_ip, v_account_draw_money_transfer, v_account_no_draw_money_transfer, v_account_first_borrow_use_money_transfer, v_borrow_id, v_borrow_name);


  /* 更新转让方资金(转让管理费: 总额减少,可用减少,可提或不可提减少) */
  set v_account_total_transfer = v_account_total_transfer - v_transfer_manage_fee;
  set v_account_usemoney_transfer = v_account_usemoney_transfer - v_transfer_manage_fee;
  /* 转让管理费先从账户受限金额扣除,不够从可提现金额扣除 */
  if v_account_no_draw_money_transfer > 0 then
    if v_account_no_draw_money_transfer >= v_transfer_manage_fee then
      set v_account_no_draw_money_transfer = v_account_no_draw_money_transfer - v_transfer_manage_fee;
    else
      set v_account_draw_money_transfer = v_account_draw_money_transfer - (v_transfer_manage_fee - v_account_no_draw_money_transfer);
      set v_account_no_draw_money_transfer = 0;
    end if;
  else
    set v_account_draw_money_transfer = v_account_draw_money_transfer - v_transfer_manage_fee;
  end if;
  /* 更新转让方资金(转让管理费: 总额减少,可用减少,可提或不可提减少) */
  update rocky_account set TOTAL = TOTAL - v_transfer_manage_fee, USE_MONEY = USE_MONEY - v_transfer_manage_fee, DRAW_MONEY = v_account_draw_money_transfer, NO_DRAW_MONEY = v_account_no_draw_money_transfer where USER_ID = v_transfer_user_id;
  /* 更新转让方资金(转让管理费: 总额减少,可用减少,可提或不可提减少) */
  insert into rocky_accountlog(USER_ID, TYPE, TOTAL, MONEY, USE_MONEY, NO_USE_MONEY, COLLECTION, TO_USER, REMARK, ADDTIME, ADDIP, DRAW_MONEY, NO_DRAW_MONEY, FIRST_BORROW_USE_MONEY, BORROW_ID, BORROW_NAME)
  values (v_transfer_user_id, 'transfer_manage_fee', v_account_total_transfer, v_transfer_manage_fee, v_account_usemoney_transfer, v_account_nousemoney_transfer, v_account_collection_transfer, v_transfer_user_id, '债权转让复审通过，扣除转让管理费。', unix_timestamp(), v_add_ip, v_account_draw_money_transfer, v_account_no_draw_money_transfer, v_account_first_borrow_use_money_transfer, v_borrow_id, v_borrow_name);


  /* 债权转让满标复审通过之后，可提金额大于净值额度，可提金额转入受限金额 */
  /* 参数依次为：用户id、借款标id,借标标标题,记录ip,净值额度表类型,资金日志表类型，资金日志表备注 */
  call deal_drawmoney_to_nodraw(v_transfer_user_id, v_borrow_id, v_borrow_name, v_add_ip, 18, 'net_draw_to_nodraw_transfer_recheck', '债权转让复审通过之后，可提金额大于净值额度，可提金额转入受限金额');


  /* 保存满标待发邮件记录 */
  insert into rocky_mail_send_record(type_id, type, `status`, addtime, email, user_id)
  select distinct s.TRANSFER_ID, 4, 0, now(), m.EMAIL, s.USER_ID
  from rocky_b_subscribe s, rocky_member m
  where s.USER_ID = m.ID
  and s.TRANSFER_ID = v_transfer_id;

  /* 操作成功 */
  set msg = '00001';
end