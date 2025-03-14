package com.hospitalApi.users.ports;

import com.hospitalApi.shared.exceptions.DuplicatedEntryException;
import com.hospitalApi.users.models.User;

public interface ForUsersPort {

    public User createUser(User newUser) throws DuplicatedEntryException;

}
