package com.carmanconsulting.smx.example.domain.sproc;

public interface SprocService
{
    <T> T executeSproc(T parameters);
}
