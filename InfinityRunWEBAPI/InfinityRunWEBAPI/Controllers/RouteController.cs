using InfinityRunWEBAPI.Models;
using InfinityRunWEBAPI.Services;
using Microsoft.AspNetCore.Mvc;

namespace InfinityRunWEBAPI.Controllers;

[ApiController]
[Route("api/[controller]")]
public class RouteController : ControllerBase
{
    private readonly RouteService _routesService;

    public RouteController(RouteService routesService) =>
        _routesService = routesService;

    
    [HttpGet]
    public async Task<List<Models.Route>> Get() =>
        await _routesService.GetAsync();

    
    [HttpGet("{id:length(24)}")]
    public async Task<ActionResult<Models.Route>> Get(string id)
    {
        var route = await _routesService.GetAsync(id);

        if (route is null)
        {
            return NotFound();
        }

        return route;
    }

    
    [HttpPost]
    public async Task<IActionResult> Post(Models.Route newRoute)
    {
        await _routesService.CreateAsync(newRoute);

        return CreatedAtAction(nameof(Get), new { id = newRoute.Id }, newRoute);
    }

    
    [HttpDelete("{id:length(24)}")]
    public async Task<IActionResult> Delete(string id)
    {
        var route = await _routesService.GetAsync(id);

        if (route is null)
        {
            return NotFound();
        }

        await _routesService.RemoveAsync(id);

        return NoContent();
    }
}