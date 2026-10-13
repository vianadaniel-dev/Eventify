package com.example.eventify.controller.web;

import com.example.eventify.model.Venue;
import com.example.eventify.service.VenueService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/venues")
public class AdminVenueController {

    private final VenueService venueService;

    public AdminVenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public String showVenuesPanel(Model model) {
        model.addAttribute("venues", venueService.getAllVenuesList());
        model.addAttribute("venue", new Venue());
        return "admin/venues";
    }

    @PostMapping
    public String saveVenue(@ModelAttribute("venue") Venue venue) {
        venueService.createVenue(venue);
        return "redirect:/admin/venues";
    }
}