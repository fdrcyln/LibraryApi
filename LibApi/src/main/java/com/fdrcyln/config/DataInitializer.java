package com.fdrcyln.config;

import com.fdrcyln.entities.Book;
import com.fdrcyln.entities.Category;
import com.fdrcyln.entities.Member;
import com.fdrcyln.entities.UserAccount;
import com.fdrcyln.enums.Role;
import com.fdrcyln.repository.BookRepository;
import com.fdrcyln.repository.ICategoryRepository;
import com.fdrcyln.repository.IMemberRepository;
import com.fdrcyln.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Value("${admin.email:admin@test.com}")
    private String adminEmail;

    @Value("${admin.password:password}")
    private String adminPassword;

    private final ICategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final IMemberRepository memberRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            ICategoryRepository categoryRepository,
            BookRepository bookRepository,
            IMemberRepository memberRepository,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 0. Seed Initial Admin Account if not exists
        if (!userAccountRepository.existsByRole(Role.ADMIN) && !userAccountRepository.existsByEmail(adminEmail)) {
            UserAccount admin = new UserAccount();
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            admin.setCreatedDate(LocalDateTime.now());
            userAccountRepository.save(admin);
        }
        // 1. Seed Categories
        List<Category> existingCategories = categoryRepository.findAll();
        
        Category yazilimCat = existingCategories.stream()
                .filter(c -> "Yazılım".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Yazılım");
                    c.setDescription("Yazılım geliştirme ve programlama dilleri üzerine kitaplar.");
                    c.setActive(true);
                    return categoryRepository.save(c);
                });

        Category romanCat = existingCategories.stream()
                .filter(c -> "Roman".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Roman");
                    c.setDescription("Klasik ve modern dünya edebiyatı romanları.");
                    c.setActive(true);
                    return categoryRepository.save(c);
                });

        Category kisiselCat = existingCategories.stream()
                .filter(c -> "Kişisel Gelişim".equalsIgnoreCase(c.getName()))
                .findFirst()
                .orElseGet(() -> {
                    Category c = new Category();
                    c.setName("Kişisel Gelişim");
                    c.setDescription("Kişisel gelişim, liderlik ve motivasyon kitapları.");
                    c.setActive(true);
                    return categoryRepository.save(c);
                });

        // 2. Seed Members
        List<Member> existingMembers = memberRepository.findAll();

        Member m1 = existingMembers.stream()
                .filter(m -> "furkan@example.com".equalsIgnoreCase(m.getEmail()))
                .findFirst()
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setFirstName("Furkan");
                    m.setLastName("Ceylan");
                    m.setEmail("furkan@example.com");
                    m.setPhone("5551234567");
                    m.setAddress("İstanbul, Türkiye");
                    m.setMembershipDate(LocalDate.now());
                    m.setActive(true);
                    return memberRepository.save(m);
                });

        Member m2 = existingMembers.stream()
                .filter(m -> "ayse@example.com".equalsIgnoreCase(m.getEmail()))
                .findFirst()
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setFirstName("Ayşe");
                    m.setLastName("Yılmaz");
                    m.setEmail("ayse@example.com");
                    m.setPhone("5559876543");
                    m.setAddress("Ankara, Türkiye");
                    m.setMembershipDate(LocalDate.now());
                    m.setActive(true);
                    return memberRepository.save(m);
                });

        Member m3 = existingMembers.stream()
                .filter(m -> "mehmet@example.com".equalsIgnoreCase(m.getEmail()))
                .findFirst()
                .orElseGet(() -> {
                    Member m = new Member();
                    m.setFirstName("Mehmet");
                    m.setLastName("Demir");
                    m.setEmail("mehmet@example.com");
                    m.setPhone("5554443322");
                    m.setAddress("İzmir, Türkiye");
                    m.setMembershipDate(LocalDate.now());
                    m.setActive(true);
                    return memberRepository.save(m);
                });

        // 3. Seed Books
        List<Book> existingBooks = bookRepository.findAll();

        if (existingBooks.stream().noneMatch(b -> "Clean Code".equalsIgnoreCase(b.getTitle()))) {
            Book b = new Book();
            b.setTitle("Clean Code");
            b.setAuthor("Robert C. Martin");
            b.setIsbn("978-0132350884");
            b.setTotalStock(5);
            b.setAvailableStock(5);
            b.setPageCount(464);
            b.setPublicationYear(2008);
            b.setCategory(yazilimCat);
            b.setActive(true);
            bookRepository.save(b);
        }

        if (existingBooks.stream().noneMatch(b -> "Design Patterns".equalsIgnoreCase(b.getTitle()))) {
            Book b = new Book();
            b.setTitle("Design Patterns");
            b.setAuthor("Erich Gamma");
            b.setIsbn("978-0201633610");
            b.setTotalStock(3);
            b.setAvailableStock(3);
            b.setPageCount(395);
            b.setPublicationYear(1994);
            b.setCategory(yazilimCat);
            b.setActive(true);
            bookRepository.save(b);
        }

        if (existingBooks.stream().noneMatch(b -> "Suç ve Ceza".equalsIgnoreCase(b.getTitle()))) {
            Book b = new Book();
            b.setTitle("Suç ve Ceza");
            b.setAuthor("Fyodor Dostoyevski");
            b.setIsbn("978-9750719387");
            b.setTotalStock(4);
            b.setAvailableStock(4);
            b.setPageCount(687);
            b.setPublicationYear(1866);
            b.setCategory(romanCat);
            b.setActive(true);
            bookRepository.save(b);
        }

        if (existingBooks.stream().noneMatch(b -> "Simyacı".equalsIgnoreCase(b.getTitle()))) {
            Book b = new Book();
            b.setTitle("Simyacı");
            b.setAuthor("Paulo Coelho");
            b.setIsbn("978-9750726439");
            b.setTotalStock(6);
            b.setAvailableStock(6);
            b.setPageCount(184);
            b.setPublicationYear(1988);
            b.setCategory(romanCat);
            b.setActive(true);
            bookRepository.save(b);
        }
    }
}
