Use master;
Create Database FlowerShop;

Use FlowerShop;

CREATE TABLE [Role](
roleID int identity(1,1) primary key,
roleName nvarchar(10) not null
)
--roleName in ('ADMIN', 'USER')

insert into [Role](roleName)
values ('ADMIN')
insert into [Role](roleName)
values ('USER')

select * from [Role]
------------------------------------------------------------------------------------------
CREATE TABLE [User](
	userID int identity(1,1) primary key,
	username nvarchar(20) NOT NULL,
	userPassword nvarchar(20) NOT NULL,
	userAddress nvarchar(100) NOT NULL,
	roleID int foreign key references [Role](roleID),
	birthdate date,
	phoneNumber nvarchar(15) NOT NULL
)


insert into [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber)
values ( 'thanh', '123456', 'Quan 9, TPHCM', 1, '2001-12-21', '09121111111')
insert into [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber)
values ( 'quyen', '123456', 'Quan 9, TPHCM', 1, '2001-04-11', '09132222222')
insert into [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber)
values ( 'tam', '123456', 'Quan 9, TPHCM', 2, '2001-02-11', '09133333333')
insert into [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber)
values ( 'phong', '123456', 'Quan 9, TPHCM', 2, '2001-07-13', '09104444444')
insert into [User](username, userPassword, userAddress, roleID, birthdate, phoneNumber)
values ( 'kien', '123456', 'Quan 9, TPHCM', 2, '2001-07-07', '09104567823')

select * from [User]

CREATE TABLE Flower (
	flowerID int IDENTITY(1,1) PRIMARY KEY,
	fowerName nvarchar(50) not null,
	flowerImage nvarchar(250) not null,
	price float,
	quantity int,
	description nvarchar(250)
)

insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Hot Romance', 61.90, 'hotromance', 3, 'A beautiful bouquet decked with dashing red roses and accompanied by white statices.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Lovesong', 55.90, 'lovesong', 3, 'A petite arrangement of ravishing spray red roses, decorated with sprays of eucalyptus gunny.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Purple Passion', 49.90, 'purplepassion', 3, 'A sweet bouquet decked with purple roses, white gerberas, dark purple statices, and white statices.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Sweet Pea', 49.90, 'sweatpea', 3, 'A bouquet of white ping pongs and pink eustomas that will remind your special recipient of their beauty and grace.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Sweet Desire', 66.90,'sweetdesire', 14, 'Decked with pink lilies, eustomas & alstroemerias, this bouquet is as sweet as it can be!')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Pink Moment', 74.90, 'pinkmoment', 14, 'A sweet arrangement of yellow lilies, shocking pink roses, and light pink carnation sprays')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Sunkissed', 57.90, 'sunkissed', '14', 'Beautiful bouquet hand wrapped to perfection. Perfect for all occasions.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Golden Sunset', 47.90, 'goldensunset', 14, 'Reminding you of the beautiful sunset, this bouquet comes with contrasting Mokara orchids in shades of red and gold.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Remember Me', 49.90, 'rememberme', 14, 'Beautiful bouquet hand wrapped to perfection. Perfect for all occasions.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Mon Amour', 64.90, 'monamour', 7, 'With pops of passionate pink hues, and laces of white petals - she is sure to be enchanted with an arrangement as marvelous as this.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Penelope', 95.90, 'penelope', 7, 'Surprise your loved ones with our beautifully arranged bouquet of imported Tulips from Holland!')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Puffy Purple', 98.90, 'puffypurple', 7, 'Beautiful sweet blooms of Hydrangeas, Matthiolas and Eustomas.')
insert into Flower(fowerName , price, flowerImage, quantity, description)
values ('Luna Rossa', 98.90, 'lunarossa', 10, 'Luna Rossa (eng. Red Moon) is beautifully wrapped hand bouquet composed of 10 red roses.')

CREATE TABLE [Order] (
	orderID int IDENTITY(1,1) PRIMARY KEY,
	orderCode nvarchar(250) not null,
	customerID int foreign key references [User](userID),
	createdDate datetime,
	total float,
	[status] int
)

CREATE TABLE OrderDetail(
	orderDetailID int identity(1,1) primary key,
	orderID int foreign key references [Order](orderID),
	flowerID int foreign key references Flower(flowerID),
	quantity int
)

