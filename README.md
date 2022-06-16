# **BanKING 2.0**

**What** it is and **How** its build.

### **Table Of Contents**
1. Introduction
2. Technologies
3. System Architecture


## Introduction
BanKING 2.0 is a paypal like application inspired by its predecessor: **BanKING**.

BanKING 2.0 is full stack application that was created because of a school project for a module called: Web Development.
During several classes of Web Development we learned how to build a full stack application from the ground up, BanKING 2.0 is a representation of what i've learned.


## Technologies
BanKING 2.0 uses several technology's to ensure Security and User Experience is at best.
To ensure our users security are all passwords **Hashed** and **Salted** with BCrypt.

### **Hash:** 
A Hash is a function that takes in any input and computes a nearly random fixed set of characters that look like a random mess. Our users passwords are hashed using BCrypt which is one of the most secure hasing algorithms as of 2022. This means that anyone with access to our database will not be able to use any of the passwords to access any accounts because hashing is 1 way only.

### **Salt:**
A Salt is a short random set of characters that is added to the password before it is passed through the hash function described above. This ensures any precomputed hashes (Rainbow Tables) are not used to reverse engineer any commonly used passwords. This ensures that not even brute forcing is worth it for any person with malicious intend.

### **JWT:**
A JWT or Json Web Token is a token that exists of a set of characters divided into 3 parts:
1. The Header
2. The Payload
3. The Signature

#### The Header:
The Header of a JWT typically contains 2 parts: The type of token and the signing algorithm that was used to sign the token

#### The Payload:
The Payload of a JWT typically contains information and claims about an entity (usually a user). In BanKING, the user along with their roles are stored inside the payload of the JWT

#### The Signature:
The Signature of a JWT is used to make sure the data inside the JWT hasnt been tempered with, this is possible with signing the JWT with a secret key. This key is then also used to authorize the token when it gets a request with the JWT.

The reason JWT fits our needs is because its scalable and it doesnt need to be stored on the database. We simply get our secret key and check if its a valid JWT, if so, we can then extract the user and role from the JWT to check if the user has access to the resource requested. This ensures and goes hand in hand with the RESTfulness of our API.

## System Architecture
