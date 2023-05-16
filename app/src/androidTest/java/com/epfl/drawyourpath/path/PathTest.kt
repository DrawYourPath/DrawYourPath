package com.epfl.drawyourpath.path

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class PathTest {
    private val initialPath: Path = Path(listOf(listOf(LatLng(1.0, 2.0), LatLng(3.0, 4.0))))

    /**
     * Test that an empty path is correctly created.
     */
    @Test
    fun testCreatingEmptyPath() {
        val path = Path()
        assertEquals(0, path.size())
    }

    /**
     * Test that creating a path form a list of section composed of points correctyl ccreated the path.
     */
    @Test
    fun testCreatingPathFromListOfPoints() {
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        val section1 = listOf(point1, point2)

        val point3 = LatLng(3.0, 6.0)
        val point4 = LatLng(3.0, 7.0)
        val point5 = LatLng(4.0, 6.0)
        val section2 = listOf(point3, point4, point5)

        val pointsSection = listOf(section1, section2)

        val path = Path(pointsSection)

        // control that the path is composed of two section
        assertEquals(5, path.size())
        assertTrue(path.getPoints().containsAll(pointsSection))
    }

    /**
     * Test if a point is correctly added to the last section of the path
     */
    @Test
    fun addPointToLastSectionCorrectly() {
        val pointAdded = LatLng(1.0, 2.0)
        // for an empty path
        val path = Path()
        path.addPointToLastSection(pointAdded)
        assertEquals(1, path.size())
        assertEquals(listOf(listOf(pointAdded)), path.getPoints())
        // for a non-empty path
        val path2 = Path(initialPath.getPoints())
        path2.addPointToLastSection(pointAdded)
        assertEquals(1, path.size())
        val expectedList = initialPath.getPointsSection(0).toMutableList()
        expectedList.add(pointAdded)
        assertEquals(listOf(expectedList), path2.getPoints())
    }

    /**
     * Test if a new section is correctly added, and that this function is initially empty
     */
    @Test
    fun addNewSectionCorrectly() {
        // for an empty path
        val path = Path()
        path.addNewSection()
        assertEquals(2, path.getPoints().size)
        assertEquals(listOf<List<LatLng>>(emptyList(), emptyList()), path.getPoints())
        // for a non-empty path
        val path2 = Path(initialPath.getPoints())
        path2.addNewSection()
        assertEquals(2, path.getPoints().size)
        val expectedList = initialPath.getPointsSection(0).toMutableList()
        assertEquals(listOf(expectedList, emptyList<LatLng>()), path2.getPoints())
    }

    /**
     * Test if adding a point after creating a new section is correctyl made
     */
    fun addPointAfterCreateSectionCorrectly() {
        val path = Path(initialPath.getPoints())
        path.addNewSection()
        val pointAdded = LatLng(1.0, 2.0)
        path.addPointToLastSection(pointAdded)
        val expectedList = initialPath.getPointsSection(0).toMutableList()
        assertEquals(listOf(expectedList, listOf(pointAdded)), path.getPoints())
    }

    /**
     * Check that an empty list is return for a new empty path
     */
    @Test
    fun getPointsReturnsAnEmptyListForANewEmptyPath() {
        val path = Path()
        assertEquals(listOf<List<LatLng>>(emptyList()), path.getPoints())
    }

    /**
     * Check that the correct list is returned for non empty path
     */
    @Test
    fun getPointsReturnsAListOfAllAddedPoints() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)

        assertEquals(listOf(listOf(point1, point2), listOf(point3, point4, point5)), path.getPoints())
    }

    /**
     * Check that list is returned for a given section of the path
     */
    @Test
    fun getPointsInSectionReturnsCorrectPointsLIstOfSection() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for section 0
        assertEquals(listOf(point1, point2), path.getPointsSection(0))
        // for section 1
        assertEquals(listOf(point3, point4, point5), path.getPointsSection(1))
    }

    /**
     * Check that an error is thrown when we would like to get the list of incorrect index section
     */
    @Test
    fun getPointsInSectionWithIncorrectIndexThrowError() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for incorrect section 2
        assertThrows(Error::class.java) {
            path.getPointsSection(2)
        }
    }

    /**
     * Check that clear removes all the points of the path
     */
    @Test
    fun clearRemovesAllPointsFromThePath() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        path.clear()
        assertTrue(path.getPoints().isEmpty())
    }

    /**
     * Check that the total size of the path returned is correct
     */
    @Test
    fun sizeReturnsTheNumberOfPointsInANonEmptyPath() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        assertEquals(5, path.size())
    }

    /**
     * Check that the size returned for each section of the path is correct
     */
    @Test
    fun sizeOfSectionReturnsTheNumberOfPointsForEachSection() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for section 0
        assertEquals(2, path.sizeOfSection(0))
        // for section 1
        assertEquals(3, path.sizeOfSection(1))
    }

    /**
     * Check that an error is thrown when we would like to get the size of incorrect index section
     */
    @Test
    fun sizeInSectionWithIncorrectIndexThrowError() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for incorrect section 2
        assertThrows(Error::class.java) {
            path.sizeOfSection(2)
        }
    }

    /**
     * Check that the distance returned for the entire path is correct
     */
    @Test
    fun getDistanceReturnsTheDistanceBetweenAddedPoints() {
        val path = Path()
        path.addPointToLastSection(LatLng(0.0, 0.0))
        path.addPointToLastSection(LatLng(0.0, 1.0))
        assertEquals(111319.9, path.getDistance(), 1.0)
    }

    /**
     * Check that the distance returned  each section of the path is correct
     */
    @Test
    fun getDistanceInSectionReturnsTheDistanceBetweenAddedPoints() {
        val path = Path()
        path.addPointToLastSection(LatLng(0.0, 0.0))
        path.addPointToLastSection(LatLng(0.0, 1.0))
        path.addNewSection()
        path.addPointToLastSection(LatLng(1.0, 1.0))
        path.addPointToLastSection(LatLng(1.0, 2.0))
        // for section 0
        assertEquals(111319.9, path.getDistanceInSection(0), 1.0)
        // for section 1
        assertEquals(111302.6, path.getDistanceInSection(1), 1.0)
    }

    /**
     * Check that an error is thrown when we would like to get the distance of incorrect index section
     */
    @Test
    fun getDistanceInSectionWithIncorrectIndexThrowError() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for incorrect section 2
        assertThrows(Error::class.java) {
            path.getDistanceInSection(2)
        }
    }

    /**
     * Check that the correct list of polyline is returned for a given path
     */
    @Test
    fun getPolylineReturnsAPolylineOptionsObject() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        val polyline = path.getPolyline()
        assertTrue(polyline is List<PolylineOptions>)
        assertEquals(2, polyline.size)
        assertEquals(listOf(point1, point2), polyline[0].points)
        assertEquals(listOf(point3, point4, point5), polyline[1].points)
    }

    /**
     * Check that the correct polyline is returned for a given section index of a path
     */
    @Test
    fun getPolylineOfSectionReturnsAPolylineOptionsObject() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for section 0
        val polyline0 = path.getPolylineInSection(0)
        assertEquals(listOf(point1, point2), polyline0.points)
        // for section 1
        val polyline1 = path.getPolylineInSection(1)
        assertEquals(listOf(point3, point4, point5), polyline1.points)
    }

    /**
     * Check that an error is thrown when we would like to get the polyline of incorrect index section
     */
    @Test
    fun getPolylineInSectionWithIncorrectIndexThrowError() {
        val path = Path()
        val point1 = LatLng(1.0, 2.0)
        val point2 = LatLng(3.0, 4.0)
        path.addPointToLastSection(point1)
        path.addPointToLastSection(point2)
        // add a new section
        path.addNewSection()
        val point3 = LatLng(5.0, 6.0)
        val point4 = LatLng(7.0, 8.0)
        val point5 = LatLng(9.0, 10.0)
        path.addPointToLastSection(point3)
        path.addPointToLastSection(point4)
        path.addPointToLastSection(point5)
        // for incorrect section 2
        assertThrows(Error::class.java) {
            path.getPolylineInSection(2)
        }
    }
}
