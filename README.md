# Mobile Point Of Sales Restaurant

<p>
  A Mobile POS system in a restaurant acts as the central hub for managing orders, payments, and customer data. 
  It connects the front-of-house (where customers place orders) with the back-of-house (kitchen or bar), ensuring smooth operations.
</p>

<ul>
   <li>Customer Interaction: A waiter or cashier inputs the order into the POS.</li>
   <li>Order Routing: The system sends the order to the correct section (e.g., a kitchen printer or kitchen display system for food, and a bar printer for drinks).</li>
   <li>Accepts cash, credit cards, digital wallets, or split payments.</li>
   <li>Integrates with payment gateways to process card transactions securely.</li>
   <li>Tracks tips and change for accurate cash flow.</li>
   <li>Captures every sale and generates reports for revenue, most popular items, and busiest times.</li>
   <li>Helps owners make data-driven decisions (e.g., removing unpopular menu items).</li>
</ul>



# Preview

<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/home.png" alt="home" />


# Features

<ol type="1">
	<li>
		Authentication
		<ol type="1">
			<li>Login</li>
			<li>Register</li>
			<li>Forgot Password</li>
			<li>Reset Password</li>
		</ol>
	</li>
	<li>
		User Account
		<ol type="1">
			<li>Change Password</li>
			<li>Manage Profile</li>
		</ol>
	</li>
	<li>
		General Page
		<ol type="1">
			<li>Home</li>
			<li>Menu Catalog</li>
			<li>Menu Detail Page</li>
			<li>Order Menu</li>
			<li>Checkout Process</li>
		</ol>
	</li>
</ol>

# Technologies Used

<ol type="1">
	<li>Visual Studio Code</li>
	<li>Android SDK </li>
	<li>Android Studio</li>
	<li>Modern Web Browser</li>
	<li>Git 2.4</li>
	<li>
		Backend Technologies
		<ol type="1">
			<li>Mongo DB</li>
			<li>Node JS LTS</li>
			<li>Express JS for REST API </li>
		</ol>
	</li>
</ol>

## Getting Started
#### 1. Clone the repository and navigate to the directory
```shell
git clone https://github.com/sandy-andryanto/kotlin-resto.git
cd kotlin-resto
```

#### 2. Install backend dependencies, please move to directory kotlin-resto/backend
```shell
npm install -g @ionic/cli
npm install nodemon -g
npm install
```

#### 3. Make a .env file and customize its settings 
```shell
APP_ENV=development
APP_TIMEZONE=Asia/Jakarta
JWT_KEY=
DB_DSN=mongodb://admin:password@localhost:27017/db
```

#### 4. Start MongoDB Service and Running REST API
```shell
# in Windows
net start MongoDB

# in Linux
sudo systemctl start mongod
sudo systemctl enable mongod

# create database
mongosh
use my_database


# running backend services
nodemon
```

#### 5. Install frontend dependencies, please move to directory kotlin-resto/frontend
```shell
npm install
```

#### 6. Create file env.properties in src/main/assets then customize its settings
```shell
APP_ENV=development
APP_TITLE=Sandy Resto"
APP_AUTHOR=Sandy Andryanto
APP_BACKEND_URL=http://10.0.2.2:8000 (10.0.2.2 for 127.0.0.1)
```

#### 7. Run Application 

<strong>To login in application, please use these credential : </strong> </br>
email : admin@administrator.example.com </br>
password : Qwerty12345#! </br>



<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/login.png" alt="login" />
<br/>
<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/splash.png" alt="store" />
</br>
<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/menu.png" alt="product" />
</br>
<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/create-order.png" alt="ordered" />
</br>
<img height="650" src="https://5an9y4lf0n50.github.io/demo-images/mobile-demo/kotlin/checkout.png" alt="checkout"/>
</br>

#### 9. Developer Contact
<ul>
	<li>
		<strong>Telegram</strong> <a target="_blank" href="https://t.me/sandy_andryanto">https://t.me/sandy_andryanto</a>
	</li>
	<li>
		<strong>Gmail</strong> <a  href="mailto:sandy.andryanto.official@gmail.com">sandy.andryanto.official@gmail.com</a>
	</li>
</ul>
