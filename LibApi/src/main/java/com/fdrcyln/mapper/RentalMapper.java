package com.fdrcyln.mapper;

import com.fdrcyln.dto.response.RentalResponse;
import com.fdrcyln.entities.Rental;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class RentalMapper {

    public RentalResponse toResponse(Rental rental) {
        RentalResponse response = new RentalResponse();

        BeanUtils.copyProperties(rental, response);

        if (rental.getBook() != null) {
            response.setBookId(rental.getBook().getId());
            response.setBookTitle(rental.getBook().getTitle());
        }

        if (rental.getMember() != null) {
            response.setMemberId(rental.getMember().getId());
            response.setMemberFullName(
                    rental.getMember().getFirstName() + " " + rental.getMember().getLastName()
            );
        }

        return response;
    }
}