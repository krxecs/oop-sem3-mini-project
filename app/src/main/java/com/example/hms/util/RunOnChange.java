package com.example.hms.util;

public class RunOnChange<T> {
  private T value;
  private final Runnable onChange;

  public RunOnChange(T value, Runnable onChange) {
    this.value = value;
    this.onChange = onChange;
  }

  public T get() {
    return value;
  }

  public void set(T value) {
    this.value = value;
    onChange.run();
  }
}
