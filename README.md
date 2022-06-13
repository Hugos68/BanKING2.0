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
To ensure our users security are all passwords **Hashed** and **Salted**.

### **Hash:** 
A Hash is a function that takes in any input and computes a nearly random fixed set of characters that look like a random mess. Our users passwords are hashed using BCrypt which is one of the most secure hasing algorithms as of 2022. This means that anyone with access to our database will not be able to use any of the passwords to access any accounts because hashing is 1 way only.

### **Salt:**
A Salt is a short random set of characters that is added to the password before its hashed. The reason we do this is to make sure precomputed hashes (Rainbow Tables) are not used to reverse engineer any commonly used passwords. This ensures that not even brute forcing is worth it for any person with malicious intend.


## System Architecture
