module com.example.hms {
  requires com.google.gson;
  requires javafx.controls;
  requires org.bouncycastle.provider;
  requires com.github.f4b6a3.uuid;
  //requires ormlite.core;
  requires ormlite.jdbc;
  requires java.sql;

  exports com.example.hms;
  exports com.example.hms.auth;
  exports com.example.hms.util.crypto;
  
  opens com.example.hms to ormlite.jdbc;
  opens com.example.hms.auth to ormlite.jdbc;
}