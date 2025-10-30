package extra;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
//
//import javax.validation.Valid;
//import javax.validation.constraints.Email;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Large, feature-rich controller for a Library Management System.
 *
 * This file intentionally contains many endpoints and helper methods so it can be used
 * as a central REST controller to manage:
 *  - Books
 *  - Authors
 *  - Publishers
 *  - Members
 *  - Loans (borrowing / returning)
 *  - Reviews
 *  - Search
 *  - Simple AI recommendation stubs
 *  - CSV import/export utilities
 *  - Basic admin endpoints & statistics
 *
 * You should integrate this controller with your existing repositories, models and security.
 *
 * NOTE: Replace or adapt repository/model field usages if your project has different fields/names.
 */
@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "http://localhost:5173")
@Validated
public class LibraryController {

    // ------------------------
    // Repositories - autowired
    // ------------------------
    @Autowired
    private com.mayura.library_management_system.repository.BookRepository bookRepo;

    @Autowired
    private com.mayura.library_management_system.repository.AuthorRepository authorRepo;

    @Autowired
    private com.mayura.library_management_system.repository.PublisherRepository publisherRepo;

    @Autowired
    private com.mayura.library_management_system.repository.MemberRepository memberRepo;

    @Autowired
    private com.mayura.library_management_system.repository.LoanRepository loanRepo;

    @Autowired
    private com.mayura.library_management_system.repository.ReviewRepository reviewRepo;

    // ------------------------
    // Basic health / info
    // ------------------------

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Library API is alive. Timestamp: " + LocalDate.now());
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> version() {
        Map<String, String> map = new HashMap<>();
        map.put("app", "Library Management System");
        map.put("version", "1.0.0-ai-enhanced");
        map.put("updated", LocalDate.now().toString());
        return ResponseEntity.ok(map);
    }

    // ------------------------
    // BOOK endpoints
    // ------------------------

    /**
     * Create or update a Book.
     * Accepts a Book object (JSON). If id exists, it updates; else creates.
     */
    @PostMapping("/books")
    public ResponseEntity<BookDTO> upsertBook(@Valid @RequestBody BookInput input) {
        // Validate references
        com.mayura.library_management_system.model.Book entity = (input.getId() == null)
                ? new com.mayura.library_management_system.model.Book()
                : bookRepo.findById(input.getId()).orElse(new com.mayura.library_management_system.model.Book());

        entity.setTitle(input.getTitle());
        entity.setIsbn(input.getIsbn());
        entity.setPublishedDate(input.getPublishedDate());
        entity.setGenre(input.getGenre());
        entity.setDescription(input.getDescription());

        if (input.getAuthorIds() != null && !input.getAuthorIds().isEmpty()) {
            List<com.mayura.library_management_system.model.Author> authors = authorRepo.findAllById(input.getAuthorIds());
            entity.setAuthors(new HashSet<>(authors));
        }

        if (input.getPublisherId() != null) {
            publisherRepo.findById(input.getPublisherId()).ifPresent(entity::setPublisher);
        }

        com.mayura.library_management_system.model.Book saved = bookRepo.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookDTO.fromEntity(saved));
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> listBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());
        Page<com.mayura.library_management_system.model.Book> p = bookRepo.findAll(pageable);
        List<BookDTO> dtos = p.stream().map(BookDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        return bookRepo.findById(id)
                .map(b -> ResponseEntity.ok(BookDTO.fromEntity(b)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        if (!bookRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        bookRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------
    // AUTHOR endpoints
    // ------------------------

    @PostMapping("/authors")
    public ResponseEntity<AuthorDTO> upsertAuthor(@Valid @RequestBody AuthorInput input) {
        com.mayura.library_management_system.model.Author entity = (input.getId() == null)
                ? new com.mayura.library_management_system.model.Author()
                : authorRepo.findById(input.getId()).orElse(new com.mayura.library_management_system.model.Author());

        entity.setName(input.getName());
        entity.setBio(input.getBio());
        entity.setEmail(input.getEmail());

        com.mayura.library_management_system.model.Author saved = authorRepo.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(AuthorDTO.fromEntity(saved));
    }

    @GetMapping("/authors")
    public ResponseEntity<List<AuthorDTO>> listAuthors() {
        List<com.mayura.library_management_system.model.Author> all = authorRepo.findAll();
        List<AuthorDTO> dtos = all.stream().map(AuthorDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<AuthorDTO> getAuthor(@PathVariable Long id) {
        return authorRepo.findById(id)
                .map(a -> ResponseEntity.ok(AuthorDTO.fromEntity(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        if (!authorRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        authorRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------
    // PUBLISHER endpoints
    // ------------------------

    @PostMapping("/publishers")
    public ResponseEntity<PublisherDTO> upsertPublisher(@Valid @RequestBody PublisherInput input) {
        com.mayura.library_management_system.model.Publisher entity = (input.getId() == null)
                ? new com.mayura.library_management_system.model.Publisher()
                : publisherRepo.findById(input.getId()).orElse(new com.mayura.library_management_system.model.Publisher());

        entity.setName(input.getName());
        entity.setAddress(input.getAddress());
        entity.setPhone(input.getPhone());
        entity.setEmail(input.getEmail());

        com.mayura.library_management_system.model.Publisher saved = publisherRepo.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(PublisherDTO.fromEntity(saved));
    }

    @GetMapping("/publishers")
    public ResponseEntity<List<PublisherDTO>> listPublishers() {
        List<com.mayura.library_management_system.model.Publisher> all = publisherRepo.findAll();
        List<PublisherDTO> dtos = all.stream().map(PublisherDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ------------------------
    // MEMBER endpoints
    // ------------------------

    @PostMapping("/members")
    public ResponseEntity<MemberDTO> upsertMember(@Valid @RequestBody MemberInput input) {
        com.mayura.library_management_system.model.Member entity = (input.getId() == null)
                ? new com.mayura.library_management_system.model.Member()
                : memberRepo.findById(input.getId()).orElse(new com.mayura.library_management_system.model.Member());

        entity.setName(input.getName());
        entity.setEmail(input.getEmail());
        entity.setPhone(input.getPhone());
        entity.setJoinedDate(input.getJoinedDate() == null ? LocalDate.now() : input.getJoinedDate());

        com.mayura.library_management_system.model.Member saved = memberRepo.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberDTO.fromEntity(saved));
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberDTO>> listMembers() {
        List<com.mayura.library_management_system.model.Member> all = memberRepo.findAll();
        List<MemberDTO> dtos = all.stream().map(MemberDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/members/{id}")
    public ResponseEntity<MemberDTO> getMember(@PathVariable Long id) {
        return memberRepo.findById(id)
                .map(m -> ResponseEntity.ok(MemberDTO.fromEntity(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ------------------------
    // LOANS (borrowing / returning)
    // ------------------------

    /**
     * Loan a book to a member.
     * - Checks: book exists and not already loaned out (or check copies availability if you have quantity)
     */
    @PostMapping("/loans/borrow")
    public ResponseEntity<LoanDTO> borrowBook(@Valid @RequestBody BorrowRequest req) {
        Optional<com.mayura.library_management_system.model.Book> bookOpt = bookRepo.findById(req.getBookId());
        Optional<com.mayura.library_management_system.model.Member> memberOpt = memberRepo.findById(req.getMemberId());

        if (!bookOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (!memberOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        com.mayura.library_management_system.model.Book book = bookOpt.get();
        com.mayura.library_management_system.model.Member member = memberOpt.get();

        // Basic availability check (expand for copy tracking)
        boolean isLoaned = loanRepo.existsByBookAndReturnedFalse(book);
        if (isLoaned) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }

        com.mayura.library_management_system.model.Loan loan = new com.mayura.library_management_system.model.Loan();
        loan.setBook(book);
        loan.setMember(member);
        loan.setBorrowedAt(LocalDate.now());
        loan.setDueAt(LocalDate.now().plusDays(req.getDays() == null ? 14 : req.getDays()));
        loan.setReturned(false);

        com.mayura.library_management_system.model.Loan saved = loanRepo.save(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanDTO.fromEntity(saved));
    }

    /**
     * Return a borrowed book.
     */
    @PostMapping("/loans/return/{loanId}")
    public ResponseEntity<LoanDTO> returnBook(@PathVariable Long loanId) {
        Optional<com.mayura.library_management_system.model.Loan> loanOpt = loanRepo.findById(loanId);
        if (!loanOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        com.mayura.library_management_system.model.Loan loan = loanOpt.get();
        if (loan.getReturned() != null && loan.getReturned()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        loan.setReturned(true);
        loan.setReturnedAt(LocalDate.now());
        com.mayura.library_management_system.model.Loan saved = loanRepo.save(loan);
        return ResponseEntity.ok(LoanDTO.fromEntity(saved));
    }

    @GetMapping("/loans")
    public ResponseEntity<List<LoanDTO>> listActiveLoans() {
        List<com.mayura.library_management_system.model.Loan> loans = loanRepo.findByReturnedFalse();
        List<LoanDTO> dtos = loans.stream().map(LoanDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ------------------------
    // REVIEWS & SENTIMENT (simple)
    // ------------------------

    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewDTO> addReview(@PathVariable Long bookId, @Valid @RequestBody ReviewInput input) {
        Optional<com.mayura.library_management_system.model.Book> bookOpt = bookRepo.findById(bookId);
        if (!bookOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        com.mayura.library_management_system.model.Review review = new com.mayura.library_management_system.model.Review();
        review.setBook(bookOpt.get());
        review.setMemberId(input.getMemberId());
        review.setText(input.getText());
        review.setRating(input.getRating());
        review.setCreatedAt(LocalDate.now());

        // naive sentiment analysis stub
        review.setSentiment(simpleSentiment(input.getText()));

        com.mayura.library_management_system.model.Review saved = reviewRepo.save(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewDTO.fromEntity(saved));
    }

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewDTO>> listReviews(@PathVariable Long bookId) {
        Optional<com.mayura.library_management_system.model.Book> bookOpt = bookRepo.findById(bookId);
        if (!bookOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        List<com.mayura.library_management_system.model.Review> reviews = reviewRepo.findByBook(bookOpt.get());
        List<ReviewDTO> dtos = reviews.stream().map(ReviewDTO::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private com.mayura.library_management_system.model.Review.Sentiment simpleSentiment(String text) {
        if (text == null || text.isBlank()) return com.mayura.library_management_system.model.Review.Sentiment.NEUTRAL;
        String t = text.toLowerCase();
        int score = 0;
        String[] positive = {"good", "great", "excellent", "love", "fantastic", "amazing", "best"};
        String[] negative = {"bad", "terrible", "awful", "worst", "hate", "poor"};

        for (String p : positive) if (t.contains(p)) score++;
        for (String n : negative) if (t.contains(n)) score--;

        if (score > 0) return com.mayura.library_management_system.model.Review.Sentiment.POSITIVE;
        if (score < 0) return com.mayura.library_management_system.model.Review.Sentiment.NEGATIVE;
        return com.mayura.library_management_system.model.Review.Sentiment.NEUTRAL;
    }

    // ------------------------
    // SEARCH endpoints (flexible)
    // ------------------------

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate publishedAfter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {

        // Simple search: combine criteria manually. For complex search use custom repo or Querydsl.
        List<com.mayura.library_management_system.model.Book> all = bookRepo.findAll();

        Stream<com.mayura.library_management_system.model.Book> stream = all.stream();

        if (!StringUtils.isEmpty(title)) {
            String t = title.toLowerCase();
            stream = stream.filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(t));
        }
        if (!StringUtils.isEmpty(author)) {
            String a = author.toLowerCase();
            stream = stream.filter(b ->
                    b.getAuthors() != null && b.getAuthors().stream()
                            .anyMatch(au -> au.getName() != null && au.getName().toLowerCase().contains(a)));
        }
        if (!StringUtils.isEmpty(genre)) {
            String g = genre.toLowerCase();
            stream = stream.filter(b -> b.getGenre() != null && b.getGenre().toLowerCase().contains(g));
        }
        if (publishedAfter != null) {
            stream = stream.filter(b -> b.getPublishedDate() != null && b.getPublishedDate().isAfter(publishedAfter));
        }

        List<BookDTO> result = stream
                .skip((long) page * size)
                .limit(size)
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ------------------------
    // SIMPLE AI Recommendation endpoints (stubs you can expand)
    // ------------------------

    /**
     * Recommend books based on a member's past borrowed books.
     * This is a simple logic-first approach: finds genres the user borrows most and recommends similar books.
     * Later you can replace this with an actual ML model or external service.
     */
    @GetMapping("/ai/recommend/member/{memberId}")
    public ResponseEntity<List<BookDTO>> recommendForMember(@PathVariable Long memberId,
                                                            @RequestParam(defaultValue = "5") int limit) {
        Optional<com.mayura.library_management_system.model.Member> memberOpt = memberRepo.findById(memberId);
        if (!memberOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        List<com.mayura.library_management_system.model.Loan> pastLoans = loanRepo.findByMember(memberOpt.get());
        Map<String, Long> genreCount = new HashMap<>();
        for (com.mayura.library_management_system.model.Loan loan : pastLoans) {
            if (loan.getBook() != null && loan.getBook().getGenre() != null) {
                genreCount.merge(loan.getBook().getGenre().toLowerCase(), 1L, Long::sum);
            }
        }

        // If no history, return random popular books
        if (genreCount.isEmpty()) {
            List<com.mayura.library_management_system.model.Book> popular = bookRepo.findTop10ByOrderByBorrowCountDesc();
            return ResponseEntity.ok(popular.stream().limit(limit).map(BookDTO::fromEntity).collect(Collectors.toList()));
        }

        // pick top genres
        List<String> topGenres = genreCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());

        List<com.mayura.library_management_system.model.Book> all = bookRepo.findAll();
        List<BookDTO> recommendations = all.stream()
                .filter(b -> b.getGenre() != null && topGenres.contains(b.getGenre().toLowerCase()))
                .filter(b -> !hasMemberBorrowedBook(memberOpt.get(), b))
                .limit(limit)
                .map(BookDTO::fromEntity)
                .collect(Collectors.toList());

        // fallback: if few recommendations, add randoms
        if (recommendations.size() < limit) {
            Collections.shuffle(all);
            for (com.mayura.library_management_system.model.Book b : all) {
                if (recommendations.size() >= limit) break;
                if (recommendations.stream().noneMatch(r -> r.getId().equals(b.getId()))) {
                    recommendations.add(BookDTO.fromEntity(b));
                }
            }
        }

        return ResponseEntity.ok(recommendations);
    }

    private boolean hasMemberBorrowedBook(com.mayura.library_management_system.model.Member member,
                                          com.mayura.library_management_system.model.Book book) {
        return loanRepo.existsByMemberAndBook(member, book);
    }

    /**
     * AI Chat endpoint (very small assistant) — stub that can be plugged to OpenAI or other LLMs.
     * Currently returns canned responses but demonstrates where to integrate.
     */
    @PostMapping("/ai/chat")
    public ResponseEntity<ChatResponse> aiChat(@Valid @RequestBody ChatRequest request) {
        // Simple rule-based small talk + knowledge about the library
        String q = request.getQuestion().toLowerCase(Locale.ROOT);

        if (q.contains("recommend") || q.contains("suggest")) {
            // Suggest popular books
            List<com.mayura.library_management_system.model.Book> popular = bookRepo.findTop10ByOrderByBorrowCountDesc();
            List<String> titles = popular.stream().limit(5).map(com.mayura.library_management_system.model.Book::getTitle).collect(Collectors.toList());
            return ResponseEntity.ok(new ChatResponse("Here are some popular books: " + String.join(", ", titles)));
        } else if (q.contains("open") || q.contains("hours")) {
            return ResponseEntity.ok(new ChatResponse("The library is open Monday-Friday 8:30AM-5:00PM"));
        } else if (q.contains("fine") || q.contains("late")) {
            return ResponseEntity.ok(new ChatResponse("Late fine is 5 units per day after the due date. Contact the admin for waivers."));
        }

        // default fallback
        return ResponseEntity.ok(new ChatResponse("Sorry, I don't understand right now. Ask about recommendations, opening hours, or fines."));
    }

    // ------------------------
    // CSV import / export utilities
    // ------------------------

    @PostMapping("/books/import")
    public ResponseEntity<ImportResult> importBooksCsv(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ImportResult(0, 0, "Empty file"));
        }

        int success = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String header = reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    String[] parts = line.split(",");
                    // naive CSV parsing: title,isbn,genre,publishedDate,description,publisherName
                    if (parts.length < 5) throw new IllegalArgumentException("Not enough columns");
                    BookInput bi = new BookInput();
                    bi.setTitle(parts[0].trim());
                    bi.setIsbn(parts[1].trim());
                    bi.setGenre(parts[2].trim());
                    if (!parts[3].trim().isEmpty()) {
                        bi.setPublishedDate(LocalDate.parse(parts[3].trim()));
                    }
                    bi.setDescription(parts[4].trim());

                    // publisher handling: naive upsert by name
                    if (parts.length >= 6 && !parts[5].trim().isEmpty()) {
                        String pname = parts[5].trim();
                        com.mayura.library_management_system.model.Publisher pub = publisherRepo.findByName(pname)
                                .orElseGet(() -> {
                                    com.mayura.library_management_system.model.Publisher p = new com.mayura.library_management_system.model.Publisher();
                                    p.setName(pname);
                                    return publisherRepo.save(p);
                                });
                        bi.setPublisherId(pub.getId());
                    }

                    upsertBook(bi);
                    success++;
                } catch (Exception ex) {
                    failed++;
                    errors.add("Line failed: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ImportResult(success, failed, "I/O error: " + e.getMessage()));
        }

        return ResponseEntity.ok(new ImportResult(success, failed, String.join("; ", errors)));
    }

    @GetMapping("/books/export")
    public ResponseEntity<InputStreamResource> exportBooksCsv() {
        List<com.mayura.library_management_system.model.Book> all = bookRepo.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("id,title,isbn,genre,publishedDate,description,publisher\n");
        for (com.mayura.library_management_system.model.Book b : all) {
            sb.append(b.getId()).append(",");
            sb.append(escapeCsv(b.getTitle())).append(",");
            sb.append(escapeCsv(b.getIsbn())).append(",");
            sb.append(escapeCsv(b.getGenre())).append(",");
            sb.append(b.getPublishedDate() == null ? "" : b.getPublishedDate()).append(",");
            sb.append(escapeCsv(b.getDescription())).append(",");
            sb.append(b.getPublisher() == null ? "" : escapeCsv(b.getPublisher().getName()));
            sb.append("\n");
        }

        InputStreamResource resource = new InputStreamResource(new java.io.ByteArrayInputStream(sb.toString().getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=books.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    private String escapeCsv(String v) {
        if (v == null) return "";
        String out = v.replace("\"", "\"\"");
        if (out.contains(",") || out.contains("\n")) {
            return "\"" + out + "\"";
        }
        return out;
    }

    // ------------------------
    // Admin & Statistics
    // ------------------------

    @GetMapping("/admin/stats")
    public ResponseEntity<AdminStats> adminStats() {
        long totalBooks = bookRepo.count();
        long totalMembers = memberRepo.count();
        long activeLoans = loanRepo.countByReturnedFalse();
        long overdue = loanRepo.countByDueAtBeforeAndReturnedFalse(LocalDate.now());

        AdminStats s = new AdminStats();
        s.setTotalBooks(totalBooks);
        s.setTotalMembers(totalMembers);
        s.setActiveLoans(activeLoans);
        s.setOverdueLoans(overdue);

        // top borrowed books
        List<com.mayura.library_management_system.model.Book> popular = bookRepo.findTop10ByOrderByBorrowCountDesc();
        s.setTopBorrowed(popular.stream().map(BookDTO::fromEntity).collect(Collectors.toList()));

        return ResponseEntity.ok(s);
    }

    // ------------------------
    // Scheduled jobs (example)
    // ------------------------

    /**
     * Example scheduled task to mark loans overdue (runs daily at 01:00).
     * Requires @EnableScheduling in a configuration class to run.
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void scheduledOverdueCheck() {
        List<com.mayura.library_management_system.model.Loan> activeLoans = loanRepo.findByReturnedFalse();
        for (com.mayura.library_management_system.model.Loan loan : activeLoans) {
            if (loan.getDueAt() != null && loan.getDueAt().isBefore(LocalDate.now())) {
                loan.setOverdue(true);
                loanRepo.save(loan);
            }
        }
    }

    // ------------------------
    // Utility endpoints & health
    // ------------------------

    @GetMapping("/books/{id}/age")
    public ResponseEntity<Map<String, Object>> bookAge(@PathVariable Long id) {
        Optional<com.mayura.library_management_system.model.Book> opt = bookRepo.findById(id);
        if (!opt.isPresent()) return ResponseEntity.notFound().build();
        com.mayura.library_management_system.model.Book b = opt.get();
        Map<String, Object> map = new HashMap<>();
        map.put("title", b.getTitle());
        if (b.getPublishedDate() != null) {
            long years = ChronoUnit.YEARS.between(b.getPublishedDate(), LocalDate.now());
            map.put("yearsSincePublished", years);
        } else {
            map.put("yearsSincePublished", null);
        }
        return ResponseEntity.ok(map);
    }

    // ------------------------
    // Exception handlers (within controller for convenience)
    // ------------------------

    @ExceptionHandler(javax.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationError(javax.validation.ConstraintViolationException ex) {
        Map<String, Object> err = new HashMap<>();
        err.put("message", "Validation failed");
        err.put("errors", ex.getConstraintViolations().stream().map(cv -> cv.getMessage()).collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralError(Exception ex) {
        Map<String, Object> err = new HashMap<>();
        err.put("message", "Server error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    // ------------------------
    // DTOs, Input classes, small helper classes
    // ------------------------

    // Book DTO to return
    public static class BookDTO {
        private Long id;
        private String title;
        private String isbn;
        private String genre;
        private LocalDate publishedDate;
        private String description;
        private Long publisherId;
        private Set<Long> authorIds;
        private Integer borrowCount;

        public static BookDTO fromEntity(com.mayura.library_management_system.model.Book b) {
            BookDTO dto = new BookDTO();
            dto.setId(b.getId());
            dto.setTitle(b.getTitle());
            dto.setIsbn(b.getIsbn());
            dto.setGenre(b.getGenre());
            dto.setPublishedDate(b.getPublishedDate());
            dto.setDescription(b.getDescription());
            dto.setBorrowCount(b.getBorrowCount() == null ? 0 : b.getBorrowCount());
            if (b.getPublisher() != null) dto.setPublisherId(b.getPublisher().getId());
            if (b.getAuthors() != null) {
                dto.setAuthorIds(b.getAuthors().stream().map(com.mayura.library_management_system.model.Author::getId).collect(Collectors.toSet()));
            } else {
                dto.setAuthorIds(new HashSet<>());
            }
            return dto;
        }

        // getters and setters
        // ... (for brevity replace with your IDE to generate)
        // Below included only essential getters/setters required elsewhere

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public LocalDate getPublishedDate() { return publishedDate; }
        public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getPublisherId() { return publisherId; }
        public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
        public Set<Long> getAuthorIds() { return authorIds; }
        public void setAuthorIds(Set<Long> authorIds) { this.authorIds = authorIds; }
        public Integer getBorrowCount() { return borrowCount; }
        public void setBorrowCount(Integer borrowCount) { this.borrowCount = borrowCount; }
    }

    // Book input for create/update
    public static class BookInput {
        private Long id;
        @NotBlank
        private String title;
        private String isbn;
        private String genre;
        private LocalDate publishedDate;
        private String description;
        private Long publisherId;
        private List<Long> authorIds;

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getIsbn() { return isbn; }
        public void setIsbn(String isbn) { this.isbn = isbn; }
        public String getGenre() { return genre; }
        public void setGenre(String genre) { this.genre = genre; }
        public LocalDate getPublishedDate() { return publishedDate; }
        public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Long getPublisherId() { return publisherId; }
        public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
        public List<Long> getAuthorIds() { return authorIds; }
        public void setAuthorIds(List<Long> authorIds) { this.authorIds = authorIds; }
    }

    // Author DTO & input
    public static class AuthorDTO {
        private Long id;
        private String name;
        private String bio;
        private String email;

        public static AuthorDTO fromEntity(com.mayura.library_management_system.model.Author a) {
            AuthorDTO dto = new AuthorDTO();
            dto.setId(a.getId());
            dto.setName(a.getName());
            dto.setBio(a.getBio());
            dto.setEmail(a.getEmail());
            return dto;
        }

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class AuthorInput {
        private Long id;
        @NotBlank
        private String name;
        private String bio;
        @Email
        private String email;

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Publisher DTO & input
    public static class PublisherDTO {
        private Long id;
        private String name;
        private String address;
        private String phone;
        private String email;

        public static PublisherDTO fromEntity(com.mayura.library_management_system.model.Publisher p) {
            PublisherDTO dto = new PublisherDTO();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setAddress(p.getAddress());
            dto.setPhone(p.getPhone());
            dto.setEmail(p.getEmail());
            return dto;
        }

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class PublisherInput {
        private Long id;
        @NotBlank
        private String name;
        private String address;
        private String phone;
        @Email
        private String email;

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Member DTO & input
    public static class MemberDTO {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private LocalDate joinedDate;

        public static MemberDTO fromEntity(com.mayura.library_management_system.model.Member m) {
            MemberDTO dto = new MemberDTO();
            dto.setId(m.getId());
            dto.setName(m.getName());
            dto.setEmail(m.getEmail());
            dto.setPhone(m.getPhone());
            dto.setJoinedDate(m.getJoinedDate());
            return dto;
        }

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getJoinedDate() { return joinedDate; }
        public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
    }

    public static class MemberInput {
        private Long id;
        @NotBlank
        private String name;
        @Email
        private String email;
        private String phone;
        private LocalDate joinedDate;

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public LocalDate getJoinedDate() { return joinedDate; }
        public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
    }

    // Loan DTO & input
    public static class LoanDTO {
        private Long id;
        private Long bookId;
        private Long memberId;
        private LocalDate borrowedAt;
        private LocalDate dueAt;
        private Boolean returned;
        private LocalDate returnedAt;
        private Boolean overdue;

        public static LoanDTO fromEntity(com.mayura.library_management_system.model.Loan l) {
            LoanDTO dto = new LoanDTO();
            dto.setId(l.getId());
            if (l.getBook() != null) dto.setBookId(l.getBook().getId());
            if (l.getMember() != null) dto.setMemberId(l.getMember().getId());
            dto.setBorrowedAt(l.getBorrowedAt());
            dto.setDueAt(l.getDueAt());
            dto.setReturned(l.getReturned());
            dto.setReturnedAt(l.getReturnedAt());
            dto.setOverdue(l.getOverdue() == null ? false : l.getOverdue());
            return dto;
        }

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public LocalDate getBorrowedAt() { return borrowedAt; }
        public void setBorrowedAt(LocalDate borrowedAt) { this.borrowedAt = borrowedAt; }
        public LocalDate getDueAt() { return dueAt; }
        public void setDueAt(LocalDate dueAt) { this.dueAt = dueAt; }
        public Boolean getReturned() { return returned; }
        public void setReturned(Boolean returned) { this.returned = returned; }
        public LocalDate getReturnedAt() { return returnedAt; }
        public void setReturnedAt(LocalDate returnedAt) { this.returnedAt = returnedAt; }
        public Boolean getOverdue() { return overdue; }
        public void setOverdue(Boolean overdue) { this.overdue = overdue; }
    }

    public static class BorrowRequest {
        @NotNull
        private Long bookId;
        @NotNull
        private Long memberId;
        private Integer days; // duration of loan

        // getters & setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
    }

    // Review DTO & input
    public static class ReviewDTO {
        private Long id;
        private Long bookId;
        private Long memberId;
        private String text;
        private Integer rating;
        private String sentiment;
        private LocalDate createdAt;

        public static ReviewDTO fromEntity(com.mayura.library_management_system.model.Review r) {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(r.getId());
            if (r.getBook() != null) dto.setBookId(r.getBook().getId());
            dto.setMemberId(r.getMemberId());
            dto.setText(r.getText());
            dto.setRating(r.getRating());
            dto.setSentiment(r.getSentiment() == null ? "NEUTRAL" : r.getSentiment().name());
            dto.setCreatedAt(r.getCreatedAt());
            return dto;
        }

        // getters & setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getSentiment() { return sentiment; }
        public void setSentiment(String sentiment) { this.sentiment = sentiment; }
        public LocalDate getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    }

    public static class ReviewInput {
        @NotNull
        private Long memberId;
        @NotBlank
        private String text;
        private Integer rating;

        // getters & setters
        public Long getMemberId() { return memberId; }
        public void setMemberId(Long memberId) { this.memberId = memberId; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
    }

    // AI chat
    public static class ChatRequest {
        @NotBlank
        private String question;

        public String getQuestion() { return question; }
        public void setQuestion(String question) { this.question = question; }
    }

    public static class ChatResponse {
        private String answer;

        public ChatResponse(String answer) { this.answer = answer; }
        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }

    // Import result
    public static class ImportResult {
        private int success;
        private int failed;
        private String message;

        public ImportResult(int success, int failed, String message) {
            this.success = success;
            this.failed = failed;
            this.message = message;
        }

        // getters & setters
        public int getSuccess() { return success; }
        public void setSuccess(int success) { this.success = success; }
        public int getFailed() { return failed; }
        public void setFailed(int failed) { this.failed = failed; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    // Admin stats
    public static class AdminStats {
        private long totalBooks;
        private long totalMembers;
        private long activeLoans;
        private long overdueLoans;
        private List<BookDTO> topBorrowed;

        // getters & setters
        public long getTotalBooks() { return totalBooks; }
        public void setTotalBooks(long totalBooks) { this.totalBooks = totalBooks; }
        public long getTotalMembers() { return totalMembers; }
        public void setTotalMembers(long totalMembers) { this.totalMembers = totalMembers; }
        public long getActiveLoans() { return activeLoans; }
        public void setActiveLoans(long activeLoans) { this.activeLoans = activeLoans; }
        public long getOverdueLoans() { return overdueLoans; }
        public void setOverdueLoans(long overdueLoans) { this.overdueLoans = overdueLoans; }
        public List<BookDTO> getTopBorrowed() { return topBorrowed; }
        public void setTopBorrowed(List<BookDTO> topBorrowed) { this.topBorrowed = topBorrowed; }
    }

    // ------------------------
    // End of controller
    // ------------------------

}
