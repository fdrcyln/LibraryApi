package com.fdrcyln.service.impl;

import com.fdrcyln.dto.request.CreateRentalRequest;
import com.fdrcyln.dto.response.RentalResponse;
import com.fdrcyln.entities.Book;
import com.fdrcyln.entities.Member;
import com.fdrcyln.entities.Rental;
import com.fdrcyln.enums.RentalStatus;
import com.fdrcyln.exception.BadRequestException;
import com.fdrcyln.exception.ResourceNotFoundException;
import com.fdrcyln.mapper.RentalMapper;
import com.fdrcyln.repository.BookRepository;
import com.fdrcyln.repository.IMemberRepository;
import com.fdrcyln.repository.IRentalRepository;
import com.fdrcyln.service.IRentalService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RentalServiceImpl implements IRentalService {

    private final IRentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final IMemberRepository memberRepository;
    private final RentalMapper rentalMapper;

    public RentalServiceImpl(
            IRentalRepository rentalRepository,
            BookRepository bookRepository,
            IMemberRepository memberRepository,
            RentalMapper rentalMapper
    ) {
        this.rentalRepository = rentalRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.rentalMapper = rentalMapper;
    }

    @Override
    @Transactional
    public RentalResponse rentBook(CreateRentalRequest request) {
        if (request.getRentalDay() == null || request.getRentalDay() < 1) {
            throw new BadRequestException("Kiralama süresi en az 1 gün olmalıdır.");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Kitap bulunamadı. ID: " + request.getBookId()));

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Üye bulunamadı. ID: " + request.getMemberId()));

        if (!Boolean.TRUE.equals(book.getActive())) {
            throw new BadRequestException("Pasif kitap kiralanamaz.");
        }

        if (!Boolean.TRUE.equals(member.getActive())) {
            throw new BadRequestException("Pasif üye kitap kiralayamaz.");
        }

        if (book.getAvailableStock() == null || book.getAvailableStock() <= 0) {
            throw new BadRequestException("Kitap stokta yok.");
        }

        // Aynı üye aynı kitabı ACTIVE durumdayken tekrar kiralayamasın
        boolean alreadyRented = rentalRepository.existsByBookIdAndMemberIdAndStatus(request.getBookId(), request.getMemberId(), RentalStatus.ACTIVE);
        if (alreadyRented) {
            throw new BadRequestException("Aynı üye aynı kitabı aktif durumdayken tekrar kiralayamaz.");
        }

        // Bir üye aynı anda en fazla 3 aktif kitap kiralayabilsin
        long activeRentalCount = rentalRepository.countByMemberIdAndStatus(request.getMemberId(), RentalStatus.ACTIVE);
        if (activeRentalCount >= 3) {
            throw new BadRequestException("Bir üye aynı anda en fazla 3 aktif kitap kiralayabilir.");
        }

        Rental rental = new Rental();
        rental.setBook(book);
        rental.setMember(member);
        rental.setRentalDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(request.getRentalDay()));
        rental.setReturnDate(null);
        rental.setStatus(RentalStatus.ACTIVE);
        rental.setActive(true);

        book.setAvailableStock(book.getAvailableStock() - 1);
        bookRepository.save(book);

        Rental savedRental = rentalRepository.save(rental);

        return rentalMapper.toResponse(savedRental);
    }

    @Override
    @Transactional
    public RentalResponse returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Kiralama kaydı bulunamadı. ID: " + rentalId));

        if (rental.getStatus() == RentalStatus.RETURNED) {
            throw new BadRequestException("Daha önce teslim edilmiş kitap tekrar teslim edilemez.");
        }

        Book book = rental.getBook();
        if (book.getAvailableStock() >= book.getTotalStock()) {
            throw new BadRequestException("Kitap iade edilemez, mevcut stok toplam stok miktarını aşamaz.");
        }

        rental.setReturnDate(LocalDate.now());
        rental.setStatus(RentalStatus.RETURNED);

        book.setAvailableStock(book.getAvailableStock() + 1);
        bookRepository.save(book);

        Rental updatedRental = rentalRepository.save(rental);

        return rentalMapper.toResponse(updatedRental);
    }

    @Override
    public List<RentalResponse> getActiveRentals() {
        return rentalRepository.findByStatus(RentalStatus.ACTIVE)
                .stream()
                .map(rentalMapper::toResponse)
                .toList();
    }

    @Override
    public List<RentalResponse> getRentalsByMember(Long memberId) {
        return rentalRepository.findByMemberId(memberId)
                .stream()
                .map(rentalMapper::toResponse)
                .toList();
    }

    @Override
    public List<RentalResponse> getLateRentals() {
        return rentalRepository.findByDueDateBeforeAndStatus(LocalDate.now(), RentalStatus.ACTIVE)
                .stream()
                .map(rental -> {
                    rental.setStatus(RentalStatus.LATE);
                    Rental updatedRental = rentalRepository.save(rental);
                    return rentalMapper.toResponse(updatedRental);
                })
                .toList();
    }
}