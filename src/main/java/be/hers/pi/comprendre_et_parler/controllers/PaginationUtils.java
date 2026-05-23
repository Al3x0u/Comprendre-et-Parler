package be.hers.pi.comprendre_et_parler.controllers;

import be.hers.pi.comprendre_et_parler.models.AppliUser;

import java.util.ArrayList;
import java.util.List;

/***
 * utility class providing generic pagination and filtering methods for lists of objects
 */
public final class PaginationUtils {

    /***
     * private constructor to prevent instanciation of this utility class
     */
    private PaginationUtils() {

    }

    /**
     * Filters a list of AppliUser objects based on a keyword matched against login, firstname and lastname
     * @param objectsToFilter The list of objects to filter, must not be null
     * @param keyword the keyword to search, empty string returns all objects
     * @return a list of objects matching the keyword
     */
    public static <T extends AppliUser> List<T> filter(List<T> objectsToFilter, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return objectsToFilter;
        }
        List<T> filtered = new ArrayList<>();
        String searchedText = keyword.trim().toLowerCase();

        for (T objectToFilter : objectsToFilter) {
            String login = objectToFilter.getLogin().toLowerCase();
            String firstName = objectToFilter.getFirstName().toLowerCase();
            String lastName = objectToFilter.getLastName().toLowerCase();

            boolean matchesLogin = login.contains(searchedText);
            boolean matchesFirstName = firstName.contains(searchedText);
            boolean matchesLastName = lastName.contains(searchedText);

            if (matchesLogin || matchesFirstName || matchesLastName) {
                filtered.add(objectToFilter);
            }
        }
        return filtered;
    }

    /***
     * calculates the total number of pages for a given number of items
     * @param totalItems the total number of items
     * @param itemsPerPage the number of items per page
     * @return the total numbers of pages, at least 1
     */
    public static int calculateTotalPages(int totalItems, int itemsPerPage){
        if (totalItems == 0) return 1;
        int totalPages = totalItems / itemsPerPage;
        if (totalItems % itemsPerPage != 0) totalPages++;
        return totalPages;
    }

    /***
     *
     * @param <T> The type of objects in the list
     * @param objects the full list of objects, must not be null
     * @param page the page number, starting at 1
     * @param itemsPerPage the number of items per page
     * @return a list of objects for the requested page
     */
    public static <T> List<T> getPage(List<T> objects, int page, int itemsPerPage){
        List<T> objectsForPage = new ArrayList<>();
        int startIndex = (page - 1) * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, objects.size());
        for (int i = startIndex; i < endIndex; i++) {
            objectsForPage.add(objects.get(i));
        }
        return objectsForPage;
    }
}
