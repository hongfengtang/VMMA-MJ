create table t_ControlBoard(
BoardID		varchar(50)		PRIMARY KEY NOT NULL,
BoardName	varchar(100),
CANBus		INTEGER			DEFAULT(0),
PortType	INTEGER			DEFAULT(0),
ServerIP	varchar(50),
Port		varchar(20),
BaudRate	varchar(10),
DataBits	varchar(5),
Parity		varchar(20),
StopBits	varchar(5),
FlowControl	varchar(20),
Deleted		varchar(1)		DEFAULT('N'),
Description VARCHAR(255)
);

create table t_EndPoint(
EndPointID	varchar(50)		PRIMARY KEY NOT NULL,
BoardID		varchar(50)		NOT NULL,
CANBusNo	INTEGER			DEFAULT(0),
NodeNo		INTEGER			DEFAULT(0),
Type		INTEGER			DEFAULT(0),
Quantity	INTEGER			DEFAULT(1),
Deleted		char(1)			DEFAULT('N'),
Description varchar(255),
FOREIGN KEY(BoardID) REFERENCES t_ControlBoard(BoardID) on delete cascade on update cascade
);

create table t_Group(
	GroupID		varchar(20) 	PRIMARY KEY NOT NULL,
	Name		varchar(100)	NOT NULL,
	Description	varchar(255)
);

create table t_User(
	UserID			varchar(50)		PRIMARY KEY NOT NULL,
	Password		varchar(100)	NOT NULL,
	FirstName		varchar(50),
	MiddleName		varchar(50),
	LastName		varchar(50),
	Gender			char(1),
	Age				int				DEFAULT(0),
	Address			varchar(200),
	Photo			varchar(100),
	GroupID			varchar(20)		NOT NULL,
	Phone1			varchar(20),
	Phone2			varchar(20),
	Phone3			varchar(20),
	Phone4			varchar(20),
	Phone5			varchar(20),
	EmergencyPerson	varchar(100),
	EmergencyPhone	varchar(20),
	CreateDateTime	DateTime,
	UpdateDateTime	DateTime,
	UpdateTimes		int DEFAULT(0),
	Deleted			char(1)			DEFAULT('N'),
	LoginTimes		int				DEFAULT(0),
	Locked			int				DEFAULT(0),
	Description		varchar(255),
	FOREIGN KEY(GroupID) REFERENCES t_Group(GroupID) on delete cascade on update cascade
);

create table t_Module(
	ModuleID		varchar(20)		PRIMARY KEY NOT NULL,
	Name			varchar(100)	NOT NULL,
	ModuleName		varchar(100) 	NOT NULL,
	ModuleClass		varchar(300) 	NOT NULL,
	ButtonName		varchar(20)	 	NOT NULL,
	Deleted			char(1)			DEFAULT('N'),
	Description		varchar(255)
);

CREATE TABLE t_Group_Module(
	GroupID		varchar(20),
	ModuleID	varchar(20),
	FOREIGN KEY(GroupID)						REFERENCES t_Group(GroupID) on delete cascade on update cascade,
	FOREIGN KEY(ModuleID) REFERENCES t_Module(ModuleID) on delete cascade on update cascade
);

create table t_Department(
	DepartmentID	varchar(50)	PRIMARY KEY NOT NULL,
	Name	varchar(100) NOT NULL,
	Address	varchar(200),
	Manager	varchar(50),
	Phone1	varchar(20),
	Phone2	varchar(20),
	Phone3	varchar(20),
	Phone4	varchar(20),
	Phone5	varchar(20),
	Deleted	char(1)	DEFAULT('N'),
	Description	varchar(255)
);

CREATE TABLE t_Medicine(
	MedicineID 		varchar(50) PRIMARY KEY NOT NULL,
	RFID																																				varchar(50),
	ScienceName												 varchar(200),
	ProductName												 varchar(200),
	Efficiency																		varchar(50),
	Type														INTEGER DEFAULT(0),
	Manufacturer												varchar(50),
	ExpiredDate												 DateTime,
	DoseUnit																								varchar(10),
	DoseRoute																		 varchar(10),
	Deleted 		char(1) DEFAULT('N'),
	Description												 varchar(255)

);

create table t_Cabinet(
	CabinetID		varchar(50) 	PRIMARY KEY NOT NULL,
	CabinetName		varchar(255) 	NOT NULL,
	Volumes			INTEGER 		DEFAULT(0),
	Deleted			char(1) 		DEFAULT('N'),
	Description		varchar(255)
);

create table t_Container(
	ContainerID	varchar(50) PRIMARY KEY NOT NULL,
	CabinetID	varchar(50) NOT NULL,
	Type		INTEGER 	DEFAULT(0),
	EndPointID	varchar(50),
	Volume		INTEGER 	DEFAULT(1),
	Seat		INTEGER 	DEFAULT(0),
	Width		INTEGER 	DEFAULT(0),
	Height		INTEGER 	DEFAULT(0),
	Deleted		char(1) 	DEFAULT('N'),
	Description	varchar(255),
	FOREIGN KEY(CabinetID) REFERENCES t_Cabinet(CabinetID) on delete cascade on update cascade
	FOREIGN KEY(EndPointID) REFERENCES t_EndPoint(EndPointID) on delete cascade on update cascade
);


create table t_Box(
	BoxID			varchar(50)	PRIMARY KEY NOT NULL,
	BoxNo			int			DEFAULT(0),
	EndPointID		varchar(50)	NOT NULL,
	LampID			INTEGER 	DEFAULT(-1),
	ContainerID		varchar(50) NOT NULL,
	MedicineID		varchar(50),
	PointX			INTEGER		NOT NULL,
	PointY			INTEGER		NOT NULL,
	Width			INTEGER		NOT NULL,
	Height			INTEGER		NOT NULL,
	Quantity		INTEGER		DEFAULT(0),
	MaxQuantity		INTEGER		DEFAULT(0),
	Alarm			INTEGER		DEFAULT(0),
	DepartmentID	varchar(50),
	Deleted			char(1) 	DEFAULT('N'),
	Description		varchar(255),
	FOREIGN KEY(EndPointID)						REFERENCES t_EndPoint(EndPointID) on delete cascade on update cascade,
	FOREIGN KEY(ContainerID) REFERENCES t_Container(ContainerID) on delete cascade on update cascade,
	FOREIGN KEY(MedicineID)						REFERENCES t_Medicine(MedicineID) on delete RESTRICT on update RESTRICT
);

create table t_Doctor(
	DoctorID		varchar(50)	PRIMARY KEY NOT NULL,
	FirstName		varchar(50),
	MiddleName		varchar(50),
	LastName		varchar(50),
	DepartmentID	varchar(50),
	FOREIGN KEY(DepartmentID) REFERENCES t_Department(DepartmentID) on delete RESTRICT on update RESTRICT
);

create table t_Patient(
	PatientID	varchar(50) PRIMARY KEY NOT NULL,
	FirstName	varchar(50), 
	MiddleName	varchar(50),
	LastName	varchar(50),
	DoctorID	varchar(50),
	FOREIGN KEY(DoctorID) REFERENCES t_Doctor(DoctorID) on delete RESTRICT on update RESTRICT
);

create table t_Patient_Medicine(
	PatientID	varchar(50),
	MedicineID	varchar(50),
	Quantity	INTEGER DEFAULT(0),
	PRIMARY KEY (PatientID, MedicineID),
	FOREIGN KEY(PatientID) REFERENCES t_Patient(PatientID) on delete RESTRICT on update RESTRICT,
	FOREIGN KEY(MedicineID) REFERENCES t_Medicine(MedicineID) on delete RESTRICT on update RESTRICT

);

create table t_ReturnMedicine(
MedicineNo	varchar(50)		NOT NULL,
PatientID	varchar(50)		NOT NULL,
Quantity	int			DEFAULT(0),
ReturnDateTime	Datetime	NOT NULL,
Operator	varchar(50)		NOT NULL
);

create table t_BoxRealtime(
	BoxID		varchar(50) PRIMARY KEY NOT NULL,
	Status		INTEGER		DEFAULT(99),
	LEDStatus	INTEGER DEFAULT(99)

);

create table t_LoginInfo(
		UserID		varchar(50),
		Event		INTEGER default(-1),
		LoginTime	datetime,
		ActionTime	datetime,
		Result		varchar(50),
	FOREIGN KEY(UserID) REFERENCES t_User(UserID) on delete RESTRICT on update RESTRICT

);

create table t_ActionInfo(
	UserID			varchar(50),
	ActionDateTime	datetime,
	Action			varchar(10),
	Result			varchar(50),
	Content			varchar(500),
	FOREIGN KEY(UserID) REFERENCES t_User(UserID) on delete RESTRICT on update RESTRICT
);


CREATE VIEW v_Providing AS
SELECT
		PM.PatientID AS PatientID,
		PM.MedicineID AS MedicineID,
		PM.Quantity AS Quantity,
		PM.ProvideDate AS ProvideDate,
		PM.ExpiredTime AS ExpiredTime,
		CASE WHEN (STRFTIME('%s', 'now', 'localtime') - STRFTIME('%s', PM.ProvideDate)) > ExpiredTime*60 THEN '1' ELSE '0' END AS IsExpired,
		PM.Around AS Around,
		P.FirstName AS PatientFirstName,
		P.MiddleName AS PatientMiddleName,
		P.LastName AS PatientLastName,
		P.DoctorID AS DoctorID,
		D.FirstName AS DoctorFirstName,
		D.MiddleName AS DoctorMiddleName,
		D.LastName AS DoctorLastName,
		D.DepartmentID AS DepartmentID,
		D.Grade AS Grade,
		DEP.Name AS DepartmentName,
		M.ScienceName AS ScienceName,
		M.ProductName AS ProductName,
		M.Efficiency AS Efficiency,
		M.Type AS Type,
		M.Manufacturer AS Manufacturer,
		M.ExpiredDate AS ExpiredDate,
		M.DoseUnit AS DoseUnit,
		M.DoseRoute AS DoseRoute,
		M.Photo AS Photo
FROM t_Patient_Medicine AS PM
LEFT OUTER JOIN t_Patient AS P ON PM.PatientID = P.PatientID
LEFT OUTER JOIN t_Doctor AS D ON P.DoctorID = D.DoctorID
LEFT OUTER JOIN t_Medicine AS M ON PM.MedicineID = M.MedicineID
LEFT OUTER JOIN t_Department AS DEP ON D.DepartmentID = DEP.DepartmentID
where PM.IsProvided = 0
	AND PM.ProvideDate IS NOT NULL
	AND	strftime('%s', PM.ProvideDate) - strftime('%s', 'now', 'localtime') < PM.Around * 60
ORDER BY ProvideDate;	
	
CREATE VIEW v_EmgProvided AS
SELECT
		D.DoctorID AS DoctorID,
		D.FirstName AS DoctorFirstName,
		D.MiddleName AS DoctorMiddleName,
		D.LastName AS DoctorLastName,
		D.DepartmentID AS DepartmentID,
		D.Grade AS Grade,
		DEP.Name AS DepartmentName,
		p.PatientID AS PatientID,
		P.FirstName AS PatientFirstName,
		P.MiddleName AS PatientMiddleName,
		P.LastName AS PatientLastName
FROM t_Doctor AS D
LEFT OUTER JOIN t_Department AS DEP ON D.DepartmentID = DEP.DepartmentID
LEFT OUTER JOIN t_Patient AS P ON D.DoctorID = P.DoctorID AND P.Deleted = 'N'
WHERE D.Deleted = 'N'
ORDER BY D.DoctorID;

CREATE VIEW v_Firmware AS
SELECT 
	CB.BoardID			AS	BoardID,
	CB.BoardName		AS	BoardName,
	CB.CANBus			AS	CANBus,
	CB.PortType			AS	PortType,
	CB.ServerIP			AS	ServerIP,
	CB.Port				AS	Port,
	CB.BaudRate			AS	BaudRate,
	CB.DataBits			AS	DataBits,
	CB.Parity			AS	Parity,
	CB.StopBits			AS	StopBits,
	CB.FlowControl		AS	FlowControl,
	EP.EndPointID		AS	BXEndPointID,
	EP.CANBusNo			AS	CANBusNo,
	EP.NodeNo			AS	NodeNo,
	EP.Type				AS	EPType,
	EP.Quantity			AS	LEDQuantity,
	CN.CabinetID		AS	CabinetID,	
	CN.CabinetName		AS	CabinetName,
	CT.ContainerID		AS	ContainerID,
	CT.Type				AS	CTType,
	CT.EndPointID		AS	CTEndPointID,
	CT.Volume			AS	Volume,
	CT.Seat				AS	CTSeat,
	CT.Width			AS	CTWidth,
	CT.Height			AS	CTHeight,
	BX.BoxID			AS	BoxID,
	BX.BoxNo			AS 	BoxNo,
	BX.LampID			AS	LampID,
	BX.MedicineID		AS	MedicineID,
	BX.PointX			AS	PointX,
	BX.PointY			AS	PointY,
	BX.Width			AS	BXWidth,
	BX.Height			AS	BXHeight,
	BX.Quantity			AS	MedQuantity,
	BX.MaxQuantity		AS	MaxQuantity,
	BX.Alarm			AS	Alarm
FROM t_Box AS BX
LEFT OUTER JOIN t_EndPoint AS EP ON EP.EndPointID = BX.EndPointID
LEFT OUTER JOIN t_Container AS CT ON CT.ContainerID = BX.ContainerID
LEFT OUTER JOIN t_Cabinet AS CN ON CN.CabinetID = CT.CabinetID
LEFT OUTER JOIN t_ControlBoard AS CB ON CB.BoardID = EP.BoardID
WHERE BX.Deleted = 'N'
ORDER BY BX.BoxID, BX.MedicineID;

CREATE VIEW v_MedicineBox AS
SELECT 
M.MedicineID	AS	MedicineID,
M.RFID			AS	RFID,
M.ScienceName	AS	ScienceName,
M.ProductName	AS	ProductName,
M.Efficiency	AS	Efficiency,
M.Type			AS	Type,
M.Manufacturer	AS	Manufacturer,
M.ExpiredDate	AS	ExpiredDate,
M.DoseUnit		AS	DoseUnit,
M.DoseRoute		AS	DoseRoute,
M.Photo			AS	Photo,
M.Deleted		AS	Deleted,
M.Description	AS	Description,
B.BoxID			AS	BoxID,
B.EndPointID	AS	EndPointID,
B.LampID		AS	LampID,
B.ContainerID	AS	ContainerID,
B.Quantity		AS	Quantity,
B.MaxQuantity	AS	MaxQuantity,
B.Alarm			AS	Alarm,
B.DepartmentID	AS	DepartmentID
FROM t_Medicine AS M
CROSS JOIN t_Box AS B ON M.MedicineID = B.MedicineID
ORDER BY M.MedicineID, B.BoxID;


CREATE VIEW v_UserLoginInfo AS
SELECT
LI.UserID		AS UserID,
LI.Event		AS Event,
LI.LoginTime	AS LoginTime,
LI.ActionTime	AS ActionTime,
LI.Result		AS Result,
US.FirstName	AS FirstName,
US.MiddleName	AS MiddleName,
US.LastName		AS LastName,
US.GroupID		AS GroupID,
GR.Name			AS GRName
FROM t_LoginInfo AS LI
LEFT OUTER JOIN t_User AS US ON LI.UserID = US.UserID
LEFT OUTER JOIN t_Group AS GR ON US.GroupID = GR.GroupID
ORDER BY LI.UserID, LI.LoginTime;


CREATE VIEW v_UserActionInfo AS
SELECT
AI.UserID				AS UserID,
AI.ActionDateTime		AS ActionDateTime,
AI.Action				AS Action,
AI.Result				AS Result,
AI.Content				AS Content,
US.FirstName			AS FirstName,
US.MiddleName			AS MiddleName,
US.LastName				AS LastName,
US.GroupID				AS GroupID,
GR.Name					AS GRName
FROM t_ActionInfo AS AI
LEFT OUTER JOIN t_User AS US ON AI.UserID = US.UserID
LEFT OUTER JOIN t_Group AS GR ON US.GroupID = GR.GroupID
ORDER BY AI.UserID, AI.ActionDateTime;










