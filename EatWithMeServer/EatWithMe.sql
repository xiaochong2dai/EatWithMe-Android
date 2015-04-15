CREATE DATABASE IF NOT EXISTS EatWithMe  DEFAULT CHARACTER SET utf8;
USE EatWithMe;
DROP TABLE IF EXISTS _2016;
CREATE TABLE _2016 (
  _id INT(11) NOT NULL AUTO_INCREMENT,
  _name VARCHAR(16) NOT NULL,
  _isOnline INT(11) NOT NULL DEFAULT '0',
  _img INT(11) NOT NULL DEFAULT '0',
  _qq INT(11) NOT NULL DEFAULT '0',
  _group INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (_id)
) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

INSERT  INTO _2016(_id,_name,_isOnline,_img,_qq,_group) 
VALUES (1,'Guan',0,2,2017,0);
INSERT  INTO _2016(_id,_name,_isOnline,_img,_qq,_group) 
VALUES(2,'Justin',1,1,2018,0);
INSERT  INTO _2016(_id,_name,_isOnline,_img,_qq,_group) 
VALUES(3,'Jeff',0,0,2019,0);

DROP TABLE IF EXISTS _2017;
CREATE TABLE _2017 (
  _id INT(11) NOT NULL AUTO_INCREMENT,
  _name VARCHAR(16) NOT NULL,
  _isOnline INT(11) NOT NULL DEFAULT '0',
  _img INT(11) NOT NULL DEFAULT '0',
  _qq INT(11) NOT NULL DEFAULT '0',
  _group INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (_id)
) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

INSERT  INTO _2017(_id,_name,_isOnline,_img,_qq,_group) 
VALUES (1,'Wade',0,3,2016,0);
INSERT  INTO _2017(_id,_name,_isOnline,_img,_qq,_group) 
VALUES(2,'Justin',1,1,2018,0),(3,'Jeff',0,0,2019,0);

DROP TABLE IF EXISTS _2018;
CREATE TABLE _2018 (
  _id INT(11) NOT NULL AUTO_INCREMENT,
  _name VARCHAR(16) NOT NULL,
  _isOnline INT(11) NOT NULL DEFAULT '0',
  _img INT(11) NOT NULL DEFAULT '0',
  _qq INT(11) NOT NULL DEFAULT '0',
  _group INT(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (_id)
) ENGINE=INNODB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
INSERT  INTO _2018(_id,_name,_isOnline,_img,_qq,_group) 
VALUES (1,'Wade',0,3,2016,0);
INSERT  INTO _2018(_id,_name,_isOnline,_img,_qq,_group) 
VALUES(2,'Guan',0,2,2017,0),(3,'Jeff',0,0,2019,0);


DROP TABLE IF EXISTS _2019;
CREATE TABLE _2019 (
  id INT(11) NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(16) NOT NULL,
  isOnline VARCHAR(5) NOT NULL,
  img INT(11) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS USER;
CREATE TABLE USER (
  _id INT(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  _name VARCHAR(50) NOT NULL COMMENT 'nickname',
  _password VARCHAR(50) NOT NULL COMMENT 'password',
  _email VARCHAR(50) NOT NULL COMMENT 'email',
  _isOnline INT(20) NOT NULL DEFAULT '0' COMMENT 'if online',
  _img INT(20) DEFAULT '0' COMMENT 'photo',
  _time VARCHAR(50) NOT NULL COMMENT 'register date',
  PRIMARY KEY (_id,_email)
) ENGINE=INNODB AUTO_INCREMENT=2031 DEFAULT CHARSET=utf8;

INSERT  INTO USER(_id,_name,_password,_email,_isOnline,_img,_time) VALUES (2016,'Wade','c4ca4238a0b923820dcc509a6f75849b','1320438999@yahoo.com',0,3,'2012-10-01  10:20:50');
INSERT  INTO USER(_id,_name,_password,_email,_isOnline,_img,_time) VALUES(2017,'Guan','c4ca4238a0b923820dcc509a6f75849b','304912256@gmail.com',0,2,'2012-10-01  10:21:32');
INSERT  INTO USER(_id,_name,_password,_email,_isOnline,_img,_time) VALUES(2018,'Justin','c4ca4238a0b923820dcc509a6f75849b','158342219@hotmail.com',1,1,'2012-10-01  10:22:16');
INSERT  INTO USER(_id,_name,_password,_email,_isOnline,_img,_time) VALUES(2019,'Jeff','c4ca4238a0b923820dcc509a6f75849b','304912256@gmail.com',0,0,'2012-10-06  17:50:50');
COMMIT;
