DROP TABLE IF EXISTS `rocky_b_transfer`;
CREATE TABLE `rocky_b_transfer` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `BORROW_ID` int(11) NOT NULL COMMENT '借款标ID',
  `BORROW_NAME` varchar(100) DEFAULT NULL COMMENT '借款标题',
  `BORROW_CREDIT_RATING` varchar(1) DEFAULT NULL COMMENT '借款标信用等级(A,B,C,D)',
  `BORROW_APR` decimal(5,2) DEFAULT NULL COMMENT '年利率',
  `BORROW_STYLE` tinyint(1) unsigned DEFAULT NULL COMMENT '还款方式',
  `BORROW_TIME_LIMIT` tinyint(3) unsigned DEFAULT NULL COMMENT '期限',
  `BORROW_TYPE` tinyint(1) unsigned DEFAULT NULL COMMENT '借款标种类（ 1：信用标，2：抵押标，3：净值标，4：秒标 5：担保标 ）',
  `BORROW_ORDER` tinyint(3) unsigned DEFAULT NULL COMMENT '期数',
  `TENDER_ID` int(11) NOT NULL COMMENT '投标ID',
  `TENDER_CAPITAL` decimal(20,2) NOT NULL COMMENT '投标金额',
  `TRANSFER_NAME` varchar(50) DEFAULT NULL COMMENT '债权转让标题',
  `TRANSFER_CONTENT` varchar(255) DEFAULT NULL COMMENT '债权转让内容',
  `TRANSFER_CREDIT_RATING` varchar(1) NOT NULL COMMENT '转让信用等级(A,B,C,D)',
  `TRANSFER_BEGIN_ORDER` tinyint(3) unsigned NOT NULL COMMENT '从第几期开始转让',
  `TIME_LIMIT` smallint(5) unsigned NOT NULL COMMENT '剩余期限',
  `TIME_LIMIT_REAL` smallint(5) unsigned NOT NULL COMMENT '实际剩余期限',
  `CAPITAL` decimal(20,2) NOT NULL COMMENT '原始投资本金',
  `INTEREST` decimal(20,2) NOT NULL COMMENT '应得利息',
  `ACCOUNT` decimal(20,2) NOT NULL COMMENT '剩余债权价值',
  `COEF` decimal(5,3) NOT NULL COMMENT '转让系数',
  `ACCOUNT_REAL` decimal(20,2) NOT NULL COMMENT '转让价格',
  `MANAGE_FEE` decimal(20,2) NOT NULL COMMENT '转让管理费',
  `INTEREST_MANAGE_FEE` decimal(20,2) NOT NULL COMMENT '所得的利息管理费',
  `INTEREST_DIFF` decimal(20,2) NOT NULL COMMENT '差额利息',
  `GAIN_LOSS` decimal(20,2) NOT NULL COMMENT '转让盈亏(= INTEREST_MANAGE_FEE + INTEREST_DIFF)',
  `MOST_ACCOUNT` decimal(12,2) NOT NULL COMMENT '最大认购金额',
  `LOWEST_ACCOUNT` decimal(12,2) NOT NULL COMMENT '最小认购金额',
  `VALID_TIME` smallint(5) NOT NULL COMMENT '有效时间(天)',
  `BID_PASSWORD` varchar(50) DEFAULT NULL COMMENT '认购密码(MD5)',
  `USER_ID` int(11) NOT NULL COMMENT '债权转让人',
  `IS_AUTOTRANSFER` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否自动投标 0：否，1：是',
  `TIMING_TIME` datetime DEFAULT NULL COMMENT '定时发标时间',
  `ADD_TIME` datetime NOT NULL COMMENT '添加时间',
  `ADD_IP` varchar(64) DEFAULT NULL COMMENT '添加IP',
  `ADD_MAC` varchar(64) DEFAULT NULL COMMENT '添加MAC',
  -- `ADD_SYSTEM` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '操作来源(0:官网,1:wap)',
  `END_TIME` datetime DEFAULT NULL COMMENT '结束时间(流标时间)',
  `SUCCESS_TIME` datetime DEFAULT NULL COMMENT '满标时间',
  `TENDER_TIMES` tinyint(4) unsigned DEFAULT '0' COMMENT '投标次数',
  `ACCOUNT_YES` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '已经借到的金额',
  `CANCEL_USER` int(11) DEFAULT NULL COMMENT '撤销人',
  `CANCEL_TIME` datetime DEFAULT NULL COMMENT '撤销时间',
  `CANCEL_IP` varchar(64) DEFAULT NULL COMMENT '撤销IP',
  `CANCEL_MAC` varchar(64) DEFAULT NULL COMMENT '撤销MAC',
  `CANCEL_REMARK` varchar(255) DEFAULT NULL COMMENT '撤销备注',
  `CONTRACT_NO` varchar(100) NOT NULL COMMENT '合同编号',
  `STATUS` tinyint(1) unsigned NOT NULL COMMENT '状态(1：新债权，审核中，2：认购中，3：满标复审中，4：转让完成，5：流标，6：被撤销 ，7：审核失败)',
  `SENDMESSAGE` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '完成后发送站内信，0不发送，1发送',
  `REMARK` varchar(255) DEFAULT NULL COMMENT '备注',
  `PLATFORM` tinyint(1) DEFAULT NULL COMMENT '平台来源(1：网页 2、微信)',
  PRIMARY KEY (`ID`),
  KEY `BORROW_ID` (`BORROW_ID`),
  KEY `TENDER_ID` (`TENDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债权转让表';


DROP TABLE IF EXISTS `rocky_b_subscribe`;
CREATE TABLE `rocky_b_subscribe` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `USER_ID` int(11) NOT NULL COMMENT '债权认购人',
  `TRANSFER_ID` int(11) NOT NULL COMMENT '债权转让ID',
  `BORROW_ID` int(11) NOT NULL COMMENT '借款标ID',
  `TENDER_ID` int(11) DEFAULT NULL COMMENT '当前认购记录最终生成到投标表的ID',
  `ACCOUNT` decimal(20,2) NOT NULL COMMENT '认购金额',
 -- `INTEREST_MANAGE_FEE` decimal(20,2) NOT NULL COMMENT '所得的利息管理费',
 -- `INTEREST_DIFF` decimal(20,2) NOT NULL COMMENT '差额利息',
 -- `GAIN_LOSS` decimal(20,2) NOT NULL COMMENT '转让盈亏(= INTEREST_MANAGE_FEE + INTEREST_DIFF)',
 -- `YESACCOUNT` decimal(20,2) NOT NULL COMMENT '最终实际支付金额',
  `REPAYMENT_CAPITAL` decimal(20,2) NOT NULL COMMENT '本金',
  `REPAYMENT_INTEREST` decimal(20,2) NOT NULL COMMENT '利息',
  `REPAYMENT_ACCOUNT` decimal(20,2) NOT NULL COMMENT '待收金额',
  `DRAW_MONEY` decimal(20,2) NOT NULL COMMENT '可提现金额',
  `NO_DRAW_MONEY` decimal(20,2) NOT NULL COMMENT '受限金额',
  `USER_LEVEL` varchar(5) NOT NULL COMMENT '用户等级',
  `RATIO` varchar(10) NOT NULL COMMENT '利息管理费比率',
  `IS_VIP` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT '是否是VIP 1:是：0：否',
  `STATUS` tinyint(1) unsigned NOT NULL COMMENT '状态(1认购中；2认购失败；3认购成功)',
  `ADD_TIME` datetime NOT NULL COMMENT '添加时间',
  `ADD_IP` varchar(64) DEFAULT NULL COMMENT '添加IP',
  `ADD_MAC` varchar(64) DEFAULT NULL COMMENT '添加MAC',
  `SUBSCRIBE_TYPE` tinyint(1) unsigned NOT NULL COMMENT '认购方式（0：手动投标，1：自动投标，2：优先投标）',
  `AUTOTENDER_ORDER` varchar(50) DEFAULT NULL COMMENT '自动投标排名位数',
  `AUTOTENDER_ORDER_REMARK` varchar(255) DEFAULT NULL COMMENT '自动排名位置不变备注',
  `PLATFORM` tinyint(1) DEFAULT NULL COMMENT '平台来源(1：网页 2、微信)',
  PRIMARY KEY (`ID`),
  KEY `TRANSFER_ID` (`TRANSFER_ID`),
  KEY `TENDER_ID` (`TENDER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债权认购表';


DROP TABLE IF EXISTS `rocky_b_transfer_approved`;
CREATE TABLE `rocky_b_transfer_approved` (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `TRANSFER_ID` int(11) NOT NULL COMMENT '转让ID',
  `STATUS` tinyint(1) NOT NULL DEFAULT 0 COMMENT '审核状态（1： 等待审核 2：初审不通过 ，3：初审通过 4： 复审不通过 5：复审通过）',
  `VERIFY_USER` int(11) DEFAULT NULL COMMENT '审核人',
  `VERIFY_IP` varchar(64) DEFAULT NULL COMMENT '审核IP',
  `VERIFY_MAC` varchar(64) DEFAULT NULL COMMENT '审核MAC',
  `VERIFY_TIME` datetime DEFAULT NULL COMMENT '审核时间',
  `VERIFY_REMARK` varchar(255) DEFAULT NULL COMMENT '审核备注',
  `VERIFY_USER2` int(11) DEFAULT NULL COMMENT '复审审核人',
  `VERIFY_IP2` varchar(64) DEFAULT NULL COMMENT '复审审核IP',
  `VERIFY_MAC2` varchar(64) DEFAULT NULL COMMENT '复审审核MAC',
  `VERIFY_TIME2` datetime DEFAULT NULL COMMENT '复审审核时间',
  `VERIFY_REMARK2` varchar(255) DEFAULT NULL COMMENT '复审审核备注',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TRANSFER_ID` (`TRANSFER_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='债权转让审核';

ALTER TABLE `rocky_b_tenderrecord`
MODIFY COLUMN `STATUS`  int(4) NOT NULL DEFAULT 0 COMMENT '投标状态（-2：已债权转让，-1：所投标失败 ，0：正在投标 ，1：所投标还款中 ，2：所投标完成结束）' AFTER `BORROW_ID`;
/**
 * 此字段已提交,见于2014-12-09_db.txt
 * ADD COLUMN `PARENT_ID`  int(11) NULL COMMENT '父ID,用于债权转让' AFTER `IS_VIP`
 */

ALTER TABLE `rocky_b_collectionrecord`
MODIFY COLUMN `STATUS`  int(4) NULL DEFAULT 0 COMMENT '标的状态,-1:已债权转让，0:尚未还款，1：已还款，2：网站垫付 ，3:垫付后还款' AFTER `ID`;
/**
 * ALTER TABLE `rocky_b_collectionrecord`
 * ADD COLUMN `TRANSFER_INTEREST` decimal(20,2) DEFAULT NULL COMMENT '债权转让应得利息' AFTER `FIRST_TENDER_REAL_ID`,
 * ADD COLUMN `TRANSFER_TIME` datetime DEFAULT NULL COMMENT '债权转让时间' AFTER `TRANSFER_INTEREST`;
 */

ALTER TABLE `rocky_mail_send_record`
MODIFY COLUMN `type`  int(2) NULL DEFAULT NULL COMMENT '类型（1：满标发邮件(borrowId)，2：注册实名认证(userId)，3：还款提醒(repaymentId)，4：债权转让满标(transferId)）' AFTER `type_id`;

ALTER TABLE `t_netvalue_log`
MODIFY COLUMN `TYPE`  int(2) NULL DEFAULT 0 COMMENT '类型【0：借款入账，1：网站奖励,2:还款扣除，3：还款入帐  4：垫付入帐  5：垫付后还款扣除   6：垫付还款后非VIP收取利息 7:提前还款扣除 8：提前还款入帐  9：收取罚息:10:直通车解锁 11:现金行权 12:借款标流标 13:借款标撤消 14:取消提现 15:撤消直通车 16:提现审核不通过 17：直通车流车 18：债权转让复审通过】' AFTER `ADDIP`;

INSERT INTO rocky_configuration (`ID`,`TYPE`, `ORDER`, `NAME`,  `VALUE`, `STATUS`, `DESC` )
select * from (
select 141 as `ID`,1 as `TYPE`, 1 as `ORDER`, 'net_draw_to_nodraw_transfer_recheck' as `NAME`, '债权转让复审通过之后，可提金额大于净值额度，可提金额转入受限金额' as `VALUE`, 0 as `STATUS`, '债权转让复审通过之后，可提金额大于净值额度，可提金额转入受限金额' as `DESC` union all
select 142,1, 1, 'transfer_success', '债权转让复审通过，转让回款成功。', 0, '债权转让复审通过，转让回款成功。' union all
select 143,1, 1, 'transfer_collection_account', '债权转让复审通过，扣除转让待收金额。', 0, '债权转让复审通过，扣除转让待收金额。' union all
select 144,1, 1, 'transfer_manage_fee', '债权转让复审通过，扣除利息管理费。', 0, '债权转让复审通过，扣除利息管理费。'
) t where not exists (select 1 from rocky_configuration c where c.ID = t.ID);


ALTER TABLE `t_netvalue_log`
MODIFY COLUMN `TYPE`  int(2) NULL DEFAULT 0 COMMENT '类型【0：借款入账，1：网站奖励,2:还款扣除，3：还款入帐  4：垫付入帐  5：垫付后还款扣除   6：垫付还款后非VIP收取利息 7:提前还款扣除 8：提前还款入帐  9：收取罚息:10:直通车解锁 11:现金行权 12:借款标流标 13:借款标撤消 14:取消提现 15:撤消直通车 16:提现审核不通过 17：直通车流车 18：债权转让复审通过 19:债权转让撤消】' AFTER `ADDIP`;

INSERT INTO rocky_configuration (`ID`,`TYPE`, `ORDER`, `NAME`,  `VALUE`, `STATUS`, `DESC` )
select * from (
select 149 as `ID`,1 as `TYPE`, 1 as `ORDER`, 'net_draw_to_nodraw_transfer_cancel' as `NAME`, '债权转让撤销之后，可提金额大于净值额度，可提金额转入受限金额' as 
`VALUE`, 0 as `STATUS`, '债权转让撤销之后，可提金额大于净值额度，可提金额转入受限金额' as `DESC` 
) t where not exists (select 1 from rocky_configuration c where c.ID = t.ID);


INSERT INTO rocky_configuration (`ID`,`TYPE`, `ORDER`, `NAME`,  `VALUE`, `STATUS`, `DESC` )
select * from (
select 150 as `ID`,1 as `TYPE`, 1 as `ORDER`, 'transfer_cold' as `NAME`, '债权转让手动认购，资金冻结' as 
`VALUE`, 0 as `STATUS`, '按手动认购方式认购，资金冻结成功。' as `DESC` union all
select 151,1, 1, 'transfer_cancel', '债权转让撤销，资金回滚', 0, '债权转让撤销，资金回滚'
) t where not exists (select 1 from rocky_configuration c where c.ID = t.ID);